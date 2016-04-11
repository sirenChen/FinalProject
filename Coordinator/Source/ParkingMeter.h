#ifndef PARKINGMETER_H
#define PARKINGMETER_H
#include "ZComDef.h"
typdef struct metermsgpkt{
     uint8 ParkingID;
     uint8 carDetection;
     uint8 payment;
     uint8 legal;
     uint8 RFIDpay;
     uint8 webpay;
     char RFID[8];
}meterMSGPKT
#endif