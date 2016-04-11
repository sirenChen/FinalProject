#include "hal_adc.h"
#include  "MT_UART.h" 
#include "adcTask.h"

//meter id
static const uint8 MeterID = 0x02;
static uint8 communication_TaskID = 0xFF;
uint8 adcTask_TaskID;

//0x00 no car there, 0x01 have car there
static uint8 isOccupy = 0x00;

// register the adc task to the communication task
// for send message
void RegisteAdc_ToSendTask(uint8 task_id)
{
  communication_TaskID = task_id;
}

// task init, will be called in OSAL
void adcTask_Init( uint8 task_id )
{
  adcTask_TaskID = task_id;
  
  //ADC init
  HalAdcInit();
  HalAdcSetReference ( HAL_ADC_REF_AVDD );
  
  //set the event flag for this task, will be process next system peroid
  osal_set_event(adcTask_TaskID, 1);
}


// task process event
UINT16 adcTask_ProcessEvent( uint8 task_id, uint16 events )
{ 
  uint16 adcReading;
  uint8 adcSend[2];
  
  //This Meter ID
  adcSend[0] = MeterID;
  adcReading = HalAdcRead ( HAL_ADC_CHANNEL_6, HAL_ADC_RESOLUTION_14 );
  
  // from no car to have car, send 't'
  if (adcReading > 1490 && isOccupy == 0) {
    //send 't' to the server
    adcSend[1] = 0x74;
    isOccupy = 0x1;
    OnBoard_SendAdc(adcSend);
  } 
  
  // from have car to no car, send 'f'
  if (adcReading <= 1490 && isOccupy == 1) {
    //send 'f' to the server
    adcSend[1] = 0x66;
    isOccupy = 0x0;
    OnBoard_SendAdc(adcSend);
  }
  
  //start the adc task 1500ms later
  osal_start_timerEx( adcTask_TaskID, 1, 2500);
        //(1500 + (osal_rand() & 0x00FF)) );
  
  return 0;
}

// send the message to the communication task
uint8 OnBoard_SendAdc( uint8 *adcSend )
{
  //adc message
  adcPkg_t *msgPtr;

  if ( communication_TaskID != 0xFF )
  {
    // Send the address to the task
    msgPtr = (adcPkg_t *)osal_msg_allocate( sizeof(adcPkg_t) );
    if ( msgPtr )
    {
      msgPtr->hdr.event = 0x50;
      msgPtr->vol = 123;
      msgPtr->adcSend[0] = adcSend[0];
      msgPtr->adcSend[1] = adcSend[1];

      //send adc message to communication task
      //also active the communication task
      osal_msg_send( communication_TaskID, (uint8 *)msgPtr );  // provide by osal layer
    }
    return 1;
  }
  else
    return 0;
}