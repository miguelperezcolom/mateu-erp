package io.mateu.erp.model.webpay;


import java.io.ByteArrayOutputStream;


public  class Base64
{
  // 0-9  A-Z  a-z  +  =
  static final byte alphabet[] = {  65,  66,  67,  68,  69,  70,  71,  72,
                                    73,  74,  75,  76,  77,  78,  79,  80,
                                    81,  82,  83,  84,  85,  86,  87,  88,
                                    89,  90,  97,  98,  99, 100, 101, 102,
                                   103, 104, 105, 106, 107, 108, 109, 110,
                                   111, 112, 113, 114, 115, 116, 117, 118,
                                   119, 120, 121, 122,  48,  49,  50,  51,
                                    52,  53,  54,  55,  56,  57,  43,  47 };
  static final byte alphabet2value[] = {  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
                                          -1,  -1,  -2,  -1,  -1,  -2,  -1,  -1,
                                          -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
                                          -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
                                          -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,
                                          -1,  -1,  -1,  62,  -1,  -1,  -1,  63,
                                          52,  53,  54,  55,  56,  57,  58,  59,
                                          60,  61,  -1,  -1,  -1,  -3,  -1,  -1,
                                          -1,   0,   1,   2,   3,   4,   5,   6,
                                           7,   8,   9,  10,  11,  12,  13,  14,
                                          15,  16,  17,  18,  19,  20,  21,  22,
                                          23,  24,  25,  -1,  -1,  -1,  -1,  -1,
                                          -1,  26,  27,  28,  29,  30,  31,  32,
                                          33,  34,  35,  36,  37,  38,  39,  40,
                                          41,  42,  43,  44,  45,  46,  47,  48,
                                          49,  50,  51,  -1,  -1,  -1,  -1,  -1 };



  public static byte[] encode(byte ab[])
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(4 * ((ab.length + 2) / 3));
    int i = 0;
    int j = 0;

    for (int k = 0; k < ab.length; k++)
    {
      i = (i << 8) + (255 & ab[k]);
      for (j += 8; j >= 6; )
      {
        j -= 6;
        baos.write(alphabet[i >>> j & 63]);
      }
    }

    if (j > 0)
    {
      switch (j)
      {
      case 2:
        baos.write(alphabet[(i & 3) << 4]);
        baos.write(61);
        baos.write(61);
        break;

      case 4:
        baos.write(alphabet[(i & 15) << 2]);
        baos.write(61);
        break;
      }
    }
    return baos.toByteArray();
  }


  public static byte[] decode(byte ab[])
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(3 * ((ab.length + 3) / 4));
    int i = 0;
    int j = 0;

    for (int k = 0; k < ab.length; k++)
    {
      byte b = ab[k];
      b = (b < 0) ? -1 : alphabet2value[b];

      switch (b)
      {
      case -1:
      case -2:
      case -3:
        break;

      default:
        i = (i << 6) + b;
        switch (++j)
        {
        case 2:
          baos.write((i & 0xFF0) >>> 4);
          break;

        case 3:
          baos.write((i & 0x3FC) >>> 2);
          break;

        case 4:
          baos.write(i & 0xFF);
          j = 0;
          break;
        }
        break;
      }
    }
    return baos.toByteArray();
  }


  public static void main(String[] args) {
    String s1 = new String(encode("abcd".getBytes()));
    System.err.println(s1);
    String s2 = "YWJjZA==";
    byte b[] = s2.getBytes();
    b[1] = 5;
    String s3 = new String(decode(b));

    System.err.println(s3);
  }


}