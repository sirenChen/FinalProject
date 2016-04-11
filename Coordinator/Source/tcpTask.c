#include"tcpTask.h"
#include "MT_UART.h" 
#include "OSAL_Timers.h"
#include "AF.h"

uint8 tcpTask_ID;
static uint8 Communication_ID=0xFF;

void tcp_request_status(void);
void tcp_adc_send(adc_t *pkt);
void tcp_RFID_send(rfid_t *pkt);

void Register_tcpTask(uint8 task_id)
{
    Communication_ID=task_id;
}
void tcpTask_Init(uint8 task_id)
{
    tcpTask_ID=task_id;
    //register task on receive task
    Register_Communication(task_id);
    osal_set_event(tcpTask_ID,TCP_REQUEST_EVENT);
}

uint16 tcpTask_ProcessEvent(uint8 task_id, uint16 events)
{
    afIncomingMSGPacket_t *MSGpkt;
    (void)task_id;
    
    if(events & TCP_REQUEST_EVENT)
    {
      tcp_request_status();
      osal_start_timerEx(tcpTask_ID,TCP_REQUEST_EVENT,1000);
      return(events^TCP_REQUEST_EVENT);
    }
    
    //HalUARTWrite(0,"ggg",3);
    if ( events & SYS_EVENT_MSG )
    {
      MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( tcpTask_ID );
      //HalUARTWrite(0,"zzz",3);
       while ( MSGpkt )
       {
          switch ( MSGpkt->hdr.event )
          {
            case onboardADC_trans:
              tcp_adc_send((adc_t *)MSGpkt);
              break;
            case onboardRFID_trans:
              tcp_RFID_send((rfid_t *)MSGpkt);
              break;
          }
          osal_msg_deallocate((uint8 *)MSGpkt);
          MSGpkt = (afIncomingMSGPacket_t *)osal_msg_receive( tcpTask_ID );
       }
    }
    return 0;
}
void tcp_request_status(void)
{
  unsigned char aa[3];
  aa[0] = 0x72;
  aa[1] = 0x72;
  aa[2] = 0x23;
  HalUARTWrite(0,aa,3);
}
void tcp_adc_send(adc_t *pkt)
{
  uint8 parkingID=pkt->parkingID;
  uint8 parkingstatus=pkt->adcReading;
  unsigned char aa[5];
  aa[0] = 0x61;//a
  aa[1] = 0x61;//a
  aa[2] = (unsigned char)(parkingID);
  aa[3] = (unsigned char)(parkingstatus);
  aa[4] = 0x23;
  HalUARTWrite(0,aa,5);
}
void tcp_RFID_send(rfid_t *pkt)
{
   unsigned char af[12];
   af[0] = 0x61;
   af[1] = 0x66;
   af[2] = pkt->ID;
   for(int i=3;i<11;i++)
   {
     af[i]=pkt->rfidReading[i-3];
   }
   af[11] = 0x23;
   HalUARTWrite(0,af,12);
}