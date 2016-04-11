#ifndef TCPTASK_H
#define TCPTASK_H
#include "ZComDef.h"
#include "OSAL.h"
#include "Communication.h"

#define TCP_REQUEST_EVENT 0X01
#define onboardMETER_INFO_SEND 0X02

#define onboardRFID_trans 0X01
#define onboardADC_trans 0X02

extern void tcpTask_Init(uint8 task_id);
extern void Register_tcpTask(uint8 task_id);
extern uint16 tcpTask_ProcessEvent(uint8 task_id, uint16 events);
#endif