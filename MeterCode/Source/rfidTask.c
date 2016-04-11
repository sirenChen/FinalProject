#include  "MT_UART.h"
#include "rfidTask.h"
#include "adcTask.h"
#include "IC.h"

//meter id
static const uint8 MeterID = 0x02;

uint8 rfidTask_TaskID;
static uint8 SampleApp_TaskID = 0xFF;

//for RFID use
uint8 asc_16[16]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
uint8 Card_Id[9]; //save card ID

// task register to communication task
// for send message use
void RegisteRfid_ToSendTask(uint8 task_id)
{
  SampleApp_TaskID = task_id;
}

void rfidTask_Init( uint8 task_id )
{
  rfidTask_TaskID = task_id;
  
  IC_Init();
  
  // set the event flag, will be processed next system peroid
  osal_set_event(rfidTask_TaskID, 1);
}

UINT16 rfidTask_ProcessEvent( uint8 task_id, uint16 events )
{ 
  IC_Init();
  //rfid process
  if(IC_Test()==1)
  {   
      // Hex Trans to ASC-II
      for(int i=0;i<4;i++)
      {
        Card_Id[i*2]=asc_16[qq[i]/16];
        Card_Id[i*2+1]=asc_16[qq[i]%16];        
      }   
      Card_Id[8]=MeterID; 
      OnBoard_SendRfid(Card_Id);
  }
  
  // set a timer for this task, run this task after 500ms
  osal_start_timerEx( rfidTask_TaskID, 1,
        (500 + (osal_rand() & 0x00FF)) );
  
  return 0;
}

// send the data to the communicaiton task
// also active the communicationt task
uint8 OnBoard_SendRfid( uint8 *rfidReading )
{
  rfidChange_t *msgPtr;

  if ( SampleApp_TaskID != 0xFF )
  {
    // Send the address to the task
    msgPtr = (rfidChange_t *)osal_msg_allocate( sizeof(rfidChange_t) );
    if ( msgPtr )
    {
      msgPtr->hdr.event = 0x60;
      for(int i=0;i<9;i++)
      {
         msgPtr->rfidReaing[i]=rfidReading[i];
      }

      // send the data to the communication task
      osal_msg_send( SampleApp_TaskID, (uint8 *)msgPtr );
    }
    return 1;
  }
  else
    return 0;
}