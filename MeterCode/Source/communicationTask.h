#ifndef SAMPLEAPP_H
#define SAMPLEAPP_H

#ifdef __cplusplus
extern "C"
{
#endif

#include "ZComDef.h"

// the description of the end point, use the sample provide by TI 
#define SAMPLEAPP_ENDPOINT           20

#define SAMPLEAPP_PROFID             0x0F08
#define SAMPLEAPP_DEVICEID           0x0001
#define SAMPLEAPP_DEVICE_VERSION     0
#define SAMPLEAPP_FLAGS              0

#define SAMPLEAPP_MAX_CLUSTERS        2
#define SAMPLEAPP_PERIODIC_CLUSTERID  1
#define SAMPLEAPP_FLASH_CLUSTERID     2
#define SAMPLEAPP_POINT_TO_POINT      4

// task init
extern void communicationTask_Init( uint8 task_id );

// task process event
extern UINT16 communicationTask_ProcessEvent( uint8 task_id, uint16 events );

/*********************************************************************
*********************************************************************/

#ifdef __cplusplus
}
#endif

#endif /* SAMPLEAPP_H */
