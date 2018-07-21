package periferico.emaus.domainlayer.bluetoothprinter;

import android.util.Log;

public class PrintingCommands {

    private final int esc = 0x1b;

    int _0=0x30; int _1=0x31; int _2=0x32; int _3=0x33; int _4=0x34; int _5=0x35; int _6=0x36; int _7=0x37; int _8=0x38; int _9=0x39;
    int A=0x41; int B=0x42; int C= 0x43; int D= 0x44; int E= 0x45; int F= 0x46; int G= 0x47; int H= 0x48; int I= 0x49; int J= 0x4A;
    int K= 0x4B; int L= 0x4C; int M= 0x4D; int N= 0x4E; int O= 0x4F; int P= 0x50; int Q= 0x51; int R= 0x52; int S= 0x53; int T= 0x54;
    int U= 0x55; int V= 0x56; int W= 0x57; int X= 0x58; int Y= 0x59; int Z= 0x5A;
    int a=0x61; int b=0x62; int c= 0x63; int d= 0x64; int e= 0x65; int f= 0x66; int g= 0x67; int h= 0x68; int i= 0x69; int j= 0x6A;
    int k= 0x6B; int l= 0x6C; int m= 0x6D; int n= 0x6E; int o= 0x6F; int p= 0x70; int q= 0x71; int r= 0x72; int s= 0x73; int t= 0x74;
    int u= 0x75; int v= 0x76; int w= 0x77; int x= 0x78; int y= 0x79; int z= 0x7A;


    int printingCommand;
    byte[] byteArray;

    public PrintingCommands(){}

    public PrintingCommands(int command){
        switch (command){
            case printLinebreak:{
                byteArray = new byte[]{esc, 0x40, esc, 0x64, 0x01};
                break;
            }

            case addLineBreak:{
                byteArray = new byte[]{0x0a};
                break;
            }

            case addEnding:{
                byteArray = new byte[]{esc, 0x40, esc, 0x56, 0x41, 0x03};
                break;
            }

            case samelinePrinting:{
                byteArray = new byte[]{esc, 0x40, esc, 0x61, 0x00, 0x41, 0x0a, esc, 0x61, 0x01, 0x41, esc, 0x61, 0x02, 0x41};
                break;
            }
        }
    }

    public void stringToPrintcode(int alignment, String hexStr) {
        byte[] pArr = new byte[6+hexStr.length()];
        pArr[0] = esc;
        pArr[1] = 0x40;
        pArr[2] = esc; pArr[3] = 0x61;
        pArr[4] = alignment==alignLeft ? (byte)0x00 : alignment==alignCenter ? (byte)0x01 : (byte)0x02;
        //pArr[4] = 0x01;

        for (int i = 0; i < hexStr.length(); i++) {
            String str = hexStr.substring(i, i + 1);
            char charStr = hexStr.charAt(i);
            pArr[i+5]= ((byte) charStr);
        }

        pArr[5+hexStr.length()] = 0x0a;
        /*
        pArr[5+hexStr.length()+1] = esc;
        pArr[5+hexStr.length()+2] = 0x40;
        pArr[5+hexStr.length()+3] = esc;
        pArr[5+hexStr.length()+4] = 0x56;
        pArr[5+hexStr.length()+5] = 0x41;
        pArr[5+hexStr.length()+6] = 0x03;
        */

        byteArray = pArr;
    }

    public static final int printLinebreak = 0;
    public static final int addLineBreak = 1;
    public static final int addEnding = 2;

    public static final int alignLeft = 0;
    public static final int alignCenter = 1;
    public static final int alignRight = 2;

    public static final int samelinePrinting = 3;
}
