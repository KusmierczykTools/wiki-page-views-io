/**
 * 
 */
package no.ntnu.idi.wikiviews.aux;

/**
 * @author tkusm
 * 
 */
public class Sharding {

	public static int DEFAULT_NUM_SHARDS = 83;

	public static int getShardNo(String text, int prime) {
		int code = 0, offset = 0;
		for (Byte b : text.getBytes()) {
			code = (code + (b + 128) * (offset + 1)) % prime;
			offset++;
		}
		return code;
	}

	public static String getShardName(int shardNo) {
		return "shard_" + String.format("%02d", shardNo);
	}

	public static String getShardName(String text, int numShards) {
		return getShardName(getShardNo(text, numShards));
	}

	public static String getShardName(String text) {
		return getShardName(text, DEFAULT_NUM_SHARDS);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "zyindex";
		System.out.println(getShardName(s));
	}

}
