#ifndef SAMPLEAPP_H
#define SAMPLEAPP_H

#ifdef __cplusplus
extern "C"
{
#endif

#include "ZComDef.h"


#define SAMPLEAPP_ENDPOINT           20

#define SAMPLEAPP_PROFID             0x0F08
#define SAMPLEAPP_DEVICEID           0x0001
#define SAMPLEAPP_DEVICE_VERSION     0
#define SAMPLEAPP_FLAGS              0

#define SAMPLEAPP_MAX_CLUSTERS        2
#define SAMPLEAPP_PERIODIC_CLUSTERID  1
#define SAMPLEAPP_FLASH_CLUSTERID     2
#define SAMPLEAPP_POINT_TO_POINT      4


// Application Events (OSAL) - These are bit weighted definitions.
#define SAMPLEAPP_SEND_PERIODIC_MSG_EVT       0x0001
  
// Group ID for Flash Command
#define SAMPLEAPP_FLASH_GROUP                  0x0001
  
// Flash Command Duration - in milliseconds
#define SAMPLEAPP_FLASH_DURATION               1000

typedef struct
{
  osal_event_hdr_t hdr;
  uint8 parkingID;
  uint8 adcReading;
} adc_t;

typedef struct
{
  osal_event_hdr_t hdr;
  uint8 ID;
  uint8 rfidReading[8]; 
} rfid_t;


extern void Communication_Init( uint8 task_id );

extern UINT16 Communication_ProcessEvent( uint8 task_id, uint16 events );

extern void Register_Communication(uint8 task_id);
/*********************************************************************
*********************************************************************/

#ifdef __cplusplus
}
#endif

#endif /* SAMPLEAPP_H */
