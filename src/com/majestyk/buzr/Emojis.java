package com.majestyk.buzr;

public class Emojis {

	// Rating Carousel
	private final static Integer[] emojis = {
		R.drawable.emoji1,	R.drawable.emoji2,	
		R.drawable.emoji3,	R.drawable.emoji4,
		R.drawable.emoji5,	R.drawable.emoji6,	
		R.drawable.emoji7,	R.drawable.emoji8,
		R.drawable.emoji9,	R.drawable.emoji11
	};

	// Leaderboard Categories
	private final static Integer[] categories = { R.drawable.leaderboard_buzr,
		R.drawable.leaderboard_emoji1,	R.drawable.leaderboard_emoji2,	
		R.drawable.leaderboard_emoji3,	R.drawable.leaderboard_emoji4,	
		R.drawable.leaderboard_emoji5,	R.drawable.leaderboard_emoji6,
		R.drawable.leaderboard_emoji7,	R.drawable.leaderboard_emoji8,	
		R.drawable.leaderboard_emoji9,	R.drawable.leaderboard_emoji11
	};

	// NotificationItem emojis
	private final static Integer[] pics = {
		R.drawable.gallery_emoji1,	R.drawable.gallery_emoji2,	
		R.drawable.gallery_emoji3,	R.drawable.gallery_emoji4,	
		R.drawable.gallery_emoji5,	R.drawable.gallery_emoji6,
		R.drawable.gallery_emoji7,	R.drawable.gallery_emoji8,	
		R.drawable.gallery_emoji9,	R.drawable.gallery_emoji11
	};
	
	private final static String[] colors = { 
		"#568eff", "#952bff", "#ff38ff", "#ff2b95", "#ff2b4e", 
		"#ff5656", "#ffaa56", "#ffff00", "#9bff38", "#00ffd4" 
	}; 

	public static Integer[] getCategories() {
		return categories;
	}

	public static Integer[] getEmojis() {
		return emojis;
	}

	public static Integer[] getPics() {
		return pics;
	}

	public static String[] getColors() {
		return colors;
	}
	
	public static int getRealPosition(int i) {
		return ((i + emojis.length) % emojis.length);
	}

}

/* Version 1.3
 *  - Removed:
 *  #10								#12
 * 	R.drawable.emoji10				R.drawable.emoji12
 *  R.drawable.leaderboard_emoji10	R.drawable.leaderboard_emoji12
 *  R.drawable.gallery_emoji10		R.drawable.gallery_emoji12
 *  "#60fd76"						"#b5fbf0"
 */