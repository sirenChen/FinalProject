#include "OSAL.h"
#include "AF.h"
#include "aps_groups.h"
#include "ZDApp.h"
#include "MT.h"
#include "OnBoard.h"
#include "adcTask.h"
#include "rfidTask.h"
#include "hal_lcd.h"
#include "hal_led.h"
#include "MT_UART.h" 
#include "communicationTask.h"

char isLegal = 'f';
int isOccupy = 0;

uint8 communication_TaskID;

// EndPoint description
endPointDesc_t MyApp_epDesc;

// Network state
devStates_t MyApp_NwkState;

// message ID
uint8 MyApp_TransID;

// Point to Point Addrss
afAddrType_t Point_To_Point_DstAddr;

// Grounp
aps_Group_t SampleApp_Group;

// This list should be filled with Application specific Cluster IDs.
const cId_t SampleApp_ClusterList[SAMPLEAPP_MAX_CLUSTERS] =
{
  SAMPLEAPP_PERIODIC_CLUSTERID,
  SAMPLEAPP_FLASH_CLUSTERID
};

const SimpleDescriptionFormat_t SampleApp_SimpleDesc =
{
  SAMPLEAPP_ENDPOINT,              //  int Endpoint;
  SAMPLEAPP_PROFID,                //  uint16 AppProfId[2];
  SAMPLEAPP_DEVICEID,              //  uint16 AppDeviceId[2];
  SAMPLEAPP_DEVICE_VERSION,        //  int   AppDevVer:4;
  SAMPLEAPP_FLAGS,                 //  int   AppFlags:4;
  SAMPLEAPP_MAX_CLUSTERS,          //  uint8  AppNumInClusters;
  (cId_t *)SampleApp_ClusterList,  //  uint8 *pAppInClusterList;
  SAMPLEAPP_MAX_CLUSTERS,          //  uint8  AppNumInClusters;
  (cId_t *)SampleApp_ClusterList   //  uint8 *pAppInClusterList;
};


void SampleApp_MessageMSGCB( afIncomingMSGPacket_t *pckt );
void SampleApp_SerialCMD(mtOSALSerialData_t* cmdMsg);

void AdcApp_Send(uint8* reading);
void RfidApp_Send(uint8* reading);
void Init_Send(void);

void display (void);

// Communication Init
// Setup network information
void communicationTask_Init( uint8 task_id )
{
  communication_TaskID = task_id;
  MyApp_NwkState = DEV_INIT;
  MyApp_TransID = 0;

  // UART Init
  MT_UartInit();
  MT_UartRegisterTaskID(task_id);
  
  // Task Register
  // Use to send message
  RegisteAdc_ToSendTask(task_id);
  RegisteRfid_ToSendTask(task_id);
  
  //Setup for the point to point destination address
  Point_To_Point_DstAddr.addrMode = (afAddrMode_t)Addr16Bit;
  Point_To_Point_DstAddr.endPoint = SAMPLEAPP_ENDPOINT;
  Point_To_Point_DstAddr.addr.shortAddr = 0x0000;

  // Fill out the endpoint description.
  MyApp_epDesc.endPoint = SAMPLEAPP_ENDPOINT;
  MyApp_epDesc.task_id = &communication_TaskID;
  MyApp_epDesc.simpleDesc
            = (SimpleDescriptionFormat_t *)&SampleApp_SimpleDesc;
  MyApp_epDesc.latencyReq = noLatencyReqs;

  // Register the endpoint description with the AF
  afRegister( &MyApp_epDesc );

  // By default, all devices start out in Group 1
  SampleApp_Group.ID = 0x0001;
  osal_memcpy( SampleApp_Group.name, "Group 1", 7  );
  aps_AddGroup( SAMPLEAPP_ENDPOINT, &SampleApp_Group );

#if defined ( LCD_SUPPORTED )
  HalLcdWriteString( "SampleApp", HAL_LCD_LINE_1 );
#endif
}

// task process event
uint16 communicationTask_ProcessEvent( uint8 task_id, uint16 events )
{
  afIncomingMSGPacket_t *MSGpkt;
  (void)task_id;  // Intentionally unreferenced parameter

  if ( events & SYS_EVENT_MSG )
  {
    // receive the message
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( communication_TaskID );
    
    // if have message
    while ( MSGpkt )
    {
      // switch which event happened
      switch ( MSGpkt->hdr.event )
      {
        // Received when a messages is received (OTA) for this endpoint
        case AF_INCOMING_MSG_CMD:
          SampleApp_MessageMSGCB( MSGpkt );
          break;

        // Received whenever the device changes state in the network
        case ZDO_STATE_CHANGE:
          MyApp_NwkState = (devStates_t)(MSGpkt->hdr.status);
          if (   (MyApp_NwkState == DEV_ROUTER)
              || (MyApp_NwkState == DEV_END_DEVICE) )
          {
            Init_Send();
            
            display();
          }
          else
          {
            // Device is no longer in the network
          }
          break;
      
      // receive message from UART
      case CMD_SERIAL_MSG:
          SampleApp_SerialCMD((mtOSALSerialData_t *)MSGpkt);
          break;
          
      //receive from ADC task 
      case 0x50:
          {
          uint8 adc[2];
          //vol = ((adcPkg_t *)(MSGpkt))->vol;
          adc[0] = ((adcPkg_t *)(MSGpkt))->adcSend[0];
          adc[1] = ((adcPkg_t *)(MSGpkt))->adcSend[1];
          
          if (adc[1] == 0x74) {
            isOccupy = 1;
          }
          
          if (adc[1] == 0x66) {
            isOccupy = 0;
          }
          
          display ();
          AdcApp_Send(adc);
          break;
          }
      
      // receive from RFID task
      case 0x60:
        {
          uint8 *rfid;
          rfid = ((rfidChange_t *)(MSGpkt))->rfidReaing;
          if (isOccupy == 1) {
            RfidApp_Send(rfid);
          }
          break;
        }
          
       default:
          break;
      }

      // Release the memory
      osal_msg_deallocate( (uint8 *)MSGpkt );

      // Next - if one is available
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( communication_TaskID );
    }

    // return unprocessed events
    return (events ^ SYS_EVENT_MSG);
  }
  return 0;
}

// display the information both from sensors and coordinator
void display () 
{
  HalLcdWriteString( "Meter ID : 2", HAL_LCD_LINE_1 );
  if (isOccupy == 0) {
    HalLcdWriteString( "No Car", HAL_LCD_LINE_2 );
    HalLcdWriteString( "", HAL_LCD_LINE_3 );
  }
  
  if (isOccupy == 1) {
    HalLcdWriteString( "Have Car", HAL_LCD_LINE_2 );
    if (isLegal == 'f') {
    HalLcdWriteString( "UNAUTHORIZED", HAL_LCD_LINE_3 );
    }
    if (isLegal == 't') {
    HalLcdWriteString( "AUTHORIZED", HAL_LCD_LINE_3 );
    }
  }
}

// after the device add into the network, send the device address
// to the coordinator
void Init_Send()
{
  uint8 *parkingID = NULL;
  *parkingID = 2;
  
  if ( AF_DataRequest( &Point_To_Point_DstAddr, &MyApp_epDesc,
                       1,
                       1,         // length
                       parkingID, // pointer header
                       &MyApp_TransID,
                       AF_DISCV_ROUTE,
                       AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
  {
  }
  
  else
  {
  }
}

// send the adc information to the coordinator
void AdcApp_Send(uint8* reading)
{
  
  if ( AF_DataRequest( &Point_To_Point_DstAddr, &MyApp_epDesc,
                       2,
                       2,       // length
                       reading, // pointer header
                       &MyApp_TransID,
                       AF_DISCV_ROUTE,
                       AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
  {
  }
  
  else
  {
  }
}


// send the RFID information to the coordinator
void RfidApp_Send(uint8* reading)
{
  if ( AF_DataRequest( &Point_To_Point_DstAddr, &MyApp_epDesc,
                       3,
                       9,        // length
                       reading,  // pointer header
                       &MyApp_TransID,
                       AF_DISCV_ROUTE,
                       AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
  {
  }
  
  else
  {
  }
}

void SampleApp_SerialCMD(mtOSALSerialData_t* cmdMsg){
  uint8 i;
  uint8 len;
  uint8 *str = NULL;
  
  str = cmdMsg->msg;
  len = *str;
  
  for(i = 1; i<len+1;i++)
  {
    HalUARTWrite(0,str+i,1);
  }
   HalUARTWrite(0,"\n",1);
  
  if ( AF_DataRequest( &Point_To_Point_DstAddr, &MyApp_epDesc,
                       15,
                       len + 1,    // length
                       str,        // pointer header
                       &MyApp_TransID,
                       AF_DISCV_ROUTE,
                       AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
  {
  }
  
  else
  {
  }
  
  
}


// receive from the coordinator
// display the information
void SampleApp_MessageMSGCB( afIncomingMSGPacket_t *pkt )
{
  
  switch ( pkt->clusterId )
  {   
    case 7:
      isLegal = pkt->cmd.Data[0];
      
      display();
      
      break;
  }
}
