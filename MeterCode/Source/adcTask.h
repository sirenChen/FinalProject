#include "ZComDef.h"

typedef struct
{
  osal_event_hdr_t hdr;
  float vol; // shift
  uint8 adcSend[2];
} adcPkg_t;

extern void adcTask_Init( uint8 task_id );
extern UINT16 adcTask_ProcessEvent( uint8 task_id, uint16 events );
extern void RegisteAdc_ToSendTask(uint8 task_id);
extern uint8 OnBoard_SendAdc( uint8 *adcSend );