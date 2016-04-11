#include "OSAL.h"
#include "ZGlobals.h"
#include "AF.h"
#include "aps_groups.h"
#include "ZDApp.h"
#include "MT.h"

#include "Communication.h"
#include "SampleAppHw.h"

#include "OnBoard.h"
#include "tcpTask.h"


/* HAL */
#include "hal_lcd.h"
#include "hal_led.h"
#include "hal_key.h"
#include "hal_adc.h"

#include  "MT_UART.h"

/*********************************************************************
 * MACROS
 */
#define PARKING_address_ini        1
#define ADC_RECEIVE                2
#define RFID_RECEIVE               3
/*********************************************************************
 * CONSTANTS
 */

/*********************************************************************
 * TYPEDEFS
 */

/*********************************************************************
 * GLOBAL VARIABLES
 */

//parking meter id
uint16 parkingID[12];

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

// This is the Endpoint/Interface description.  It is defined here, but
// filled-in in SampleApp_Init().  Another way to go would be to fill
// in the structure here and make it a "const" (in code space).  The
// way it's defined in this sample app it is define in RAM.
endPointDesc_t SampleApp_epDesc;

/*********************************************************************
 * EXTERNAL VARIABLES
 */

/*********************************************************************
 * EXTERNAL FUNCTIONS
 */

/*********************************************************************
 * LOCAL VARIABLES
 */
uint8 SampleApp_TaskID;   // Task ID for internal task/event processing
                          // This variable will be received when
                          // SampleApp_Init() is called.
devStates_t SampleApp_NwkState;

uint8 SampleApp_TransID;  // This is the unique message ID (counter)

afAddrType_t Point_To_Point_DstAddr;       //point to point

aps_Group_t SampleApp_Group;

static uint8 tcpTask_ID=0xFF;

uint8 SampleAppPeriodicCounter = 0;
uint8 SampleAppFlashCounter = 0;

//project
uint16 endDivceAdd;

/*********************************************************************
 * LOCAL FUNCTIONS
 */
void SampleApp_MessageMSGCB( afIncomingMSGPacket_t *pkt );
void Parkingmeter_MessageMSGCB( afIncomingMSGPacket_t *pkt);
uint8 onboardADC_MessageMSGCB( afIncomingMSGPacket_t *pkt);
uint8 onboardRFID_MessageMSGCB( afIncomingMSGPacket_t *pkt);
void SampleApp_SerialCMD(mtOSALSerialData_t* cmdMsg);
void Register_SampleApp(uint8 task_id);
/*********************************************************************
 * NETWORK LAYER CALLBACKS
 */

/*********************************************************************
 * PUBLIC FUNCTIONS
 */
void Register_Communication(uint8 task_id)
{
  tcpTask_ID=task_id;
}


void Communication_Init( uint8 task_id )
{
  SampleApp_TaskID = task_id;
  SampleApp_NwkState = DEV_INIT;
  SampleApp_TransID = 0;
  
  MT_UartInit();
  MT_UartRegisterTaskID(task_id);
  HalUARTWrite(0,"Hello World\n",12);
  
  // Fill out the endpoint description.
  SampleApp_epDesc.endPoint = SAMPLEAPP_ENDPOINT;
  SampleApp_epDesc.task_id = &SampleApp_TaskID;
  SampleApp_epDesc.simpleDesc
            = (SimpleDescriptionFormat_t *)&SampleApp_SimpleDesc;
  SampleApp_epDesc.latencyReq = noLatencyReqs;

  // Register the endpoint description with the AF
  afRegister( &SampleApp_epDesc );

  // Register for all key events - This app will handle all key events
  RegisterForKeys( SampleApp_TaskID );

  // By default, all devices start out in Group 1
  SampleApp_Group.ID = 0x0001;
  osal_memcpy( SampleApp_Group.name, "Group 1", 7  );
  aps_AddGroup( SAMPLEAPP_ENDPOINT, &SampleApp_Group );

#if defined ( LCD_SUPPORTED )
  HalLcdWriteString( "SampleApp", HAL_LCD_LINE_1 );
#endif
}



uint16 Communication_ProcessEvent( uint8 task_id, uint16 events )
{
  afIncomingMSGPacket_t *MSGpkt;
  (void)task_id;  // Intentionally unreferenced parameter

  if ( events & SYS_EVENT_MSG )
  {
    MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( SampleApp_TaskID );
    while ( MSGpkt )
    {
      switch ( MSGpkt->hdr.event )
      {

        // Receive message from parking meter
        case AF_INCOMING_MSG_CMD:
          Parkingmeter_MessageMSGCB( MSGpkt );
          break;
          
         case CMD_SERIAL_MSG:
          SampleApp_SerialCMD((mtOSALSerialData_t *)MSGpkt);
          break;
      
        // Received whenever the device changes state in the network
        case ZDO_STATE_CHANGE:
          SampleApp_NwkState = (devStates_t)(MSGpkt->hdr.status);
          if ( //(SampleApp_NwkState == DEV_ZB_COORD)
               (SampleApp_NwkState == DEV_ROUTER)
              || (SampleApp_NwkState == DEV_END_DEVICE) )
          {
          }
          else
          {
            // Device is no longer in the network
          }
          break;
          
        default:
          break;
      }

      // Release the memory
      osal_msg_deallocate( (uint8 *)MSGpkt );

      // Next - if one is available
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( SampleApp_TaskID );
    }

    // return unprocessed events
    return (events ^ SYS_EVENT_MSG);
  }

  return 0;
}

void Parkingmeter_MessageMSGCB( afIncomingMSGPacket_t *pkt)
{
  switch(pkt->clusterId)
  {
    case PARKING_address_ini:
      parkingID[(int)(pkt->cmd.Data[0])]=pkt->srcAddr.addr.shortAddr;
        
      break;
    case ADC_RECEIVE:
      onboardADC_MessageMSGCB(pkt);
      break;
    case RFID_RECEIVE:
      onboardRFID_MessageMSGCB(pkt);
      break;
      case 15:
      int a=pkt->cmd.Data[0];
      unsigned char aa;
      aa=(unsigned char)(a+48);
      HalUARTWrite(0,"ID:",3);
      HalUARTWrite(0,&aa,1);
      HalUARTWrite(0,"\n",1);
      afAddrType_t toend; 
      toend.addr.shortAddr= parkingID[1];
      toend.addrMode = (afAddrMode_t)Addr16Bit;
      toend.endPoint = SAMPLEAPP_ENDPOINT;
            if ( AF_DataRequest( &toend, &SampleApp_epDesc,
                       7,
                       1,//length
                       &aa,
                       &SampleApp_TransID,
                       AF_DISCV_ROUTE,
                       AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
  {
  }
  
  else
  {
  }
      break;
  }
}

uint8 onboardADC_MessageMSGCB( afIncomingMSGPacket_t *pkt)
{
  uint8 ID=pkt->cmd.Data[0];
  uint8 status=pkt->cmd.Data[1];
  adc_t *msgPtr;
  if(tcpTask_ID != 0xFF)
  {
    msgPtr = (adc_t*)osal_msg_allocate(sizeof(adc_t));
    if(msgPtr)
    {
      msgPtr->hdr.event = onboardADC_trans;
      msgPtr->parkingID = ID;
      msgPtr->adcReading = status;
      
      osal_msg_send(tcpTask_ID,(uint8 *)msgPtr);
    }
    return 1;
  }
  else 
    return 0;
}

uint8 onboardRFID_MessageMSGCB( afIncomingMSGPacket_t *pkt)
{
  rfid_t *msgPtr;
  if(tcpTask_ID != 0xFF)
  {
    msgPtr = (rfid_t*)osal_msg_allocate(sizeof(rfid_t));
    if(msgPtr)
    {
      msgPtr->hdr.event = onboardRFID_trans;
      for(int i=0;i<8;i++)
      {
        msgPtr->rfidReading[i]=pkt->cmd.Data[i];
      }
      msgPtr->ID=pkt->cmd.Data[8];
      osal_msg_send(tcpTask_ID,(uint8 *)msgPtr);
    }
    return 1;
  }
  else 
    return 0;
}

void SampleApp_SerialCMD(mtOSALSerialData_t* cmdMsg){
  uint8 meterId;
  uint8 len;
  uint8 *str = NULL;
  str = cmdMsg->msg;
  len = *str;
  if(len==11){
    meterId = ((int)(*(cmdMsg->msg+1)));
    afAddrType_t toend; 
    toend.addr.shortAddr= parkingID[meterId];
    toend.addrMode = (afAddrMode_t)Addr16Bit;
    toend.endPoint = SAMPLEAPP_ENDPOINT;
  
    
    if ( AF_DataRequest( &toend, &SampleApp_epDesc,
                        7,
                        1,
                        (cmdMsg->msg+3),
                        &SampleApp_TransID,
                        AF_DISCV_ROUTE,
                        AF_DEFAULT_RADIUS ) == afStatus_SUCCESS )
    {
    }
    
    else
    {
    }
  }
  
}