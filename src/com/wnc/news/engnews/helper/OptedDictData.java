package com.wnc.news.engnews.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.widget.act.abs.AutoCompletable;
import word.DicWord;
import android.annotation.SuppressLint;

import com.wnc.news.act.ActTeam;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.engnews.kpi.SelectedWord;

@SuppressLint("DefaultLocale")
public class OptedDictData
{
	private static Stack<DicWord> seekWordList = new Stack<DicWord>();
	private static Map<Integer, CharSequence> wordExpandContentMap = new HashMap<Integer, CharSequence>();
	private static List<AutoCompletable> items = new ArrayList<AutoCompletable>();
	private static Set<SelectedWord> latelyWords = new HashSet<SelectedWord>();

	public static void setLatelyWords(Set<SelectedWord> lws)
	{
		latelyWords = lws;
	}

	public static Set<SelectedWord> getLatelyWords()
	{
		return latelyWords;
	}

	/**
	 * 自动完成下拉框的数据
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static List<AutoCompletable> getActItems()
	{
		if (items.size() > 0)
		{
			return items;
		}
		items.add(new ActTeam("arsenal", "阿森纳"));
		items.add(new ActTeam("barcelona", "巴塞罗那"));
		items.add(new ActTeam("manchester-united", "曼联"));
		items.add(new ActTeam("manchester-city", "曼城"));
		items.add(new ActTeam("tottenham-hotspur", "热刺"));
		items.add(new ActTeam("real-madrid", "皇马"));
		items.add(new ActTeam("liverpool", "利物浦"));
		items.add(new ActTeam("san-antonio-spurs", "马刺"));
		items.add(new ActTeam("golden-state-warriors", "勇士"));
		items.add(new ActTeam("skysports", "天空体育"));
		items.add(new ActTeam("squawka", ""));
		items.add(new ActTeam("basketballinsiders", "深度篮球"));
		items.add(new ActTeam("reddit", ""));
		items.add(new ActTeam("soccer", "足球"));
		items.add(new ActTeam("nba", "篮球"));
		items.add(new ActTeam(
				"-------------------------------------------------", "华丽的分割线"));
		final List<DicWord> cetDictWords = new ArrayList<DicWord>(
				DictionaryDao.getCetDictWords());
		Collections.sort(cetDictWords, new Comparator<DicWord>()
		{
			@Override
			public int compare(DicWord arg0, DicWord arg1)
			{
				return arg0.getBase_word().toLowerCase()
						.compareTo(arg1.getBase_word().toLowerCase());
			}
		});
		for (DicWord dic : cetDictWords)
		{
			final String cn_mean = dic.getCn_mean();
			int len = cn_mean.length();
			String mean2 = len > 15 ? cn_mean.substring(0, 15) + "..."
					: cn_mean;
			items.add(new ActTeam(dic.getBase_word(), mean2));
		}
		return items;
	}

	public static void addSeekWord(DicWord dicWord)
	{
		getSeekWordList().push(dicWord);
	}

	public static Stack<DicWord> getSeekWordList()
	{
		return seekWordList;
	}

	public static void addWordExpand(Integer topicID, CharSequence cs)
	{
		getWordExpandContentMap().put(topicID, cs);
	}

	public static Map<Integer, CharSequence> getWordExpandContentMap()
	{
		return wordExpandContentMap;
	}
}
