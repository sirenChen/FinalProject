#include "variable.h"
#include "IC_w_r.h"

#include "MT_UART.h"

uchar qq[4];
uchar buf[4];

void Initial(void);
uchar IC_Test(void);
void IC_Init(void);


void Initial(void)
{
    CLKCONCMD &= ~0x40;                           //Set System Clock Source 32Mhz 
    while(CLKCONSTA & 0x40);                      //Wait the Clock Source stable
    CLKCONCMD &= ~0x47;                           //Set the System Clock to 32Mhz 

    
  /*  IC_CS P1_7 */
   P1DIR |= 1<<7;
   P1INP |= 1<<7;
   P1SEL &= ~(1<<7);
   
   /* IC_SCK  P0_1 */
   P0DIR |= 1<<1;
   P0INP |= 1<<1;
   P0SEL &= ~(1<<1);
   
      /* IC_MOSI P1_2 */
   P1DIR |= 1<<2;
   P1INP |= 1<<2;
   P1SEL &= ~(1<<2);
   
      /* IC_MISO P0_4 */
   P0DIR &= ~(1<<4);
   P0INP &= ~(1<<4);
   P0SEL &= ~(1<<4);
   
      /* IC_REST P0_5 */
   P0DIR |= 1<<5;
   P0INP |= 1<<5;
   P0SEL &= ~(1<<5);
  
  IC_SCK = 1;
  IC_CS = 1;
}


uchar IC_Test(void)
{
  //uint i;
  uchar find=0xaa;
  uchar ar;   
  
  //Find RFID Card
  ar = PcdRequest(0x52,buf);
  
  if(ar != 0x26) 
      ar = PcdRequest(0x52,buf);
  if(ar != 0x26)
      find = 0xaa;
  if((ar == 0x26)&&(find == 0xaa)) {
    //avoid collision
    if(PcdAnticoll(qq) == 0x26) {
      find = 0x00;
      return 1;
    }
  }
   return 0;
 
}

void IC_Init(void)
{
  Initial();
  PcdReset();
  M500PcdConfigISOType('A');
}