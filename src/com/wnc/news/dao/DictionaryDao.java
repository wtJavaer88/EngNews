package com.wnc.news.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import word.DicWord;
import word.Topic;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import common.uihelper.MyAppParams;
import common.utils.WordSplit;

public class DictionaryDao
{
    static SQLiteDatabase database;
    private static List<String> wordAndChars;

    public static void openDatabase()
    {
        try
        {
            String databaseFilename = MyAppParams.DICTIONARY_DB;
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
                    null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void closeDatabase()
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    public static boolean isConnect()
    {
        return database != null && database.isOpen();
    }

    static Set<DicWord> topics = new HashSet<DicWord>();

    public synchronized static void initTopics()
    {
        if (topics.size() > 0)
        {
            return;
        }
        try
        {
            openDatabase();

            String sql = "select e.*,d.topic_word,d.mean_cn FROM word_exchange E LEFT JOIN dictionary D ON E.topic_id=D.topic_id";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                // word_third word_done word_pl word_ing word_past word_er
                // word_est
                DicWord dicWord = new DicWord();
                dicWord.setBase_word(c.getString(c.getColumnIndex("topic_word")));
                dicWord.setTopic_id(c.getString(c.getColumnIndex("topic_id")));
                dicWord.setWord_third(c.getString(c
                        .getColumnIndex("word_third")));
                dicWord.setWord_done(c.getString(c.getColumnIndex("word_done")));
                dicWord.setWord_er(c.getString(c.getColumnIndex("word_er")));
                dicWord.setWord_est(c.getString(c.getColumnIndex("word_est")));
                dicWord.setWord_ing(c.getString(c.getColumnIndex("word_ing")));
                dicWord.setWord_pl(c.getString(c.getColumnIndex("word_pl")));
                dicWord.setWord_past(c.getString(c.getColumnIndex("word_past")));
                dicWord.setCn_mean(c.getString(c.getColumnIndex("mean_cn")));
                topics.add(dicWord);
                c.moveToNext();
            }
            // topics.remove("target");
            // topics.remove("guy");
            // topics.remove("blank");
            // topics.remove("span");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
    }

    public static Set<Topic> findCETWords(String dialog)
    {
        Set<Topic> finds = new HashSet<Topic>();
        wordAndChars = WordSplit
                .getWordAndChars(dialog.replaceAll("<.*?>", ""));
        for (String string : wordAndChars)
        {
            if (string.trim().length() > 3)
            {
                for (DicWord t : topics)
                {
                    final Topic topic = new Topic(t);
                    if (t.getBase_word().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getBase_word());
                        topic.setState("getBase_word");
                        finds.add(topic);
                    }
                    else if (t.getWord_er().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_er());
                        topic.setState("word_er");
                        finds.add(topic);
                    }
                    else if (t.getWord_done().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_done());
                        topic.setState("word_done");
                        finds.add(topic);
                    }
                    else if (t.getWord_est().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_est());
                        topic.setState("word_est");
                        finds.add(topic);
                    }
                    else if (t.getWord_ing().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_ing());
                        topic.setState("word_ing");
                        finds.add(topic);
                    }
                    else if (t.getWord_past().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_past());
                        topic.setState("word_past");
                        finds.add(topic);
                    }
                    else if (t.getWord_pl().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_pl());
                        topic.setState("word_pl");
                        finds.add(topic);
                    }
                    else if (t.getWord_third().equalsIgnoreCase(string.trim()))
                    {
                        topic.setMatched_word(t.getWord_third());
                        topic.setState("word_third");
                        finds.add(topic);
                    }
                }
            }
        }
        return finds;
    }
}
