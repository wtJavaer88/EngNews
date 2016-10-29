package com.wnc.news.engnews.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import word.DicWord;

public class OptedDictData
{
	private static Stack<DicWord> seekWordList = new Stack<DicWord>();
	private static Map<Integer, CharSequence> wordExpandContentMap = new HashMap<Integer, CharSequence>();

	public static void addSeekWord(DicWord dicWord)
	{
		getSeekWordList().add(dicWord);
	}

	public static void addWordExpand(Integer topicID, CharSequence cs)
	{
		getWordExpandContentMap().put(topicID, cs);
	}

	public static Stack<DicWord> getSeekWordList()
	{
		return seekWordList;
	}

	public static Map<Integer, CharSequence> getWordExpandContentMap()
	{
		return wordExpandContentMap;
	}
}
