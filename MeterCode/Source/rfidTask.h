#include "ZComDef.h"

typedef struct
{
  osal_event_hdr_t hdr;
  uint8 rfidReaing[9]; // shift
} rfidChange_t;


extern void rfidTask_Init( uint8 task_id );
extern UINT16 rfidTask_ProcessEvent( uint8 task_id, uint16 events );
extern void RegisteRfid_ToSendTask(uint8 task_id);
extern uint8 OnBoard_SendRfid( uint8 *rfidReading );