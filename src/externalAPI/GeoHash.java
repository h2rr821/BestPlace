package externalAPI;
//http://developer-should-know.tumblr.com/post/87283491372/geohash-encoding-and-decoding-algorithm
//A copy from http://developer-should-know.com/post/87283491372/geohash-encoding-and-decoding-algorithm
public class GeoHash {

	private static final String BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    private static int divideRangeByValue(double value, double[] range) {
        double mid = middle(range);
        if (value >= mid) {
            range[0] = mid;
            return 1;
        } else {
            range[1] = mid;
            return 0;
        }
    }
    
    private static double middle(double[] range) {
        return (range[0] + range[1]) / 2;
    }

    public static String encodeGeohash(double latitude, double longitude, int precision) {
        double[] latRange = new double[]{-90.0, 90.0};
        double[] lonRange = new double[]{-180.0, 180.0};
        boolean isEven = true;
        int bit = 0;
        int base32CharIndex = 0;
        StringBuilder geohash = new StringBuilder();

        while (geohash.length() < precision) {
            if (isEven) {
                base32CharIndex = (base32CharIndex << 1) | divideRangeByValue(longitude, lonRange);
            } else {
                base32CharIndex = (base32CharIndex << 1) | divideRangeByValue(latitude, latRange);
            }

            isEven = !isEven;

            if (bit < 4) {
                bit++;
            } else {
                geohash.append(BASE_32.charAt(base32CharIndex));
                bit = 0;
                base32CharIndex = 0;
            }
        }

        return geohash.toString();
    }

    private static void divideRangeByBit(int bit, double[] range) {
        double mid = middle(range);
        if (bit > 0) {
            range[0] = mid;
        } else {
            range[1] = mid;
        }
    }
    
    
    public static double[] decodeGeohash(String geohash) {
        double[] latRange = new double[]{-90.0, 90.0};
        double[] lonRange = new double[]{-180.0, 180.0};
        boolean isEvenBit = true;

        for (int i = 0; i < geohash.length(); i++) {
            int base32CharIndex = BASE_32.indexOf(geohash.charAt(i));
            for (int j = 4; j >= 0; j--) {
                if (isEvenBit) {
                    divideRangeByBit((base32CharIndex >> j) & 1, lonRange);
                } else {
                    divideRangeByBit((base32CharIndex >> j) & 1, latRange);
                }
                isEvenBit = !isEvenBit;
            }
        }

        return new double[]{middle(latRange), middle(lonRange)};
    }
    
    
    //test in main======================================
    
    
    public static void main(String[] args) {
    	// Expect to see 'u4pruydqqvj8'
    	System.out.println(encodeGeohash(57.64911, 10.40744, 12));
    	//double[] locate=decodeGeohash("u4pruydqqvj8");
    	//9vk0ze84
    	double[] locate=decodeGeohash("9qh3ed7u");
    	System.out.println(locate[0]+","+locate[1]);
    	//34.026803970336914,-117.61430740356445
    }

    
}
