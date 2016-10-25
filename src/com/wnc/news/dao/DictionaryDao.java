package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import word.DicWord;
import word.Topic;
import word.WordExpand;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import common.uihelper.MyAppParams;
import common.utils.WordSplit;

public class DictionaryDao
{
    static SQLiteDatabase database;
    private static List<String> wordAndChars;
    static Logger log = Logger.getLogger(DictionaryDao.class);

    public static synchronized void openDatabase()
    {
        try
        {
            String databaseFilename = MyAppParams.DICTIONARY_DB;
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
                    null);
        }
        catch (Exception e)
        {
            log.error(e);

        }
    }

    public static synchronized void closeDatabase()
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    public static synchronized boolean isConnect()
    {
        return database != null && database.isOpen();
    }

    static Set<DicWord> cetDicWords = new HashSet<DicWord>();

    public synchronized static DicWord findWord(String word)
    {
        DicWord dicWord = null;
        word = word.toLowerCase().toString();
        try
        {
            openDatabase();

            String sql = "select e.*,d.topic_word,d.mean_cn FROM  dictionary D LEFT JOIN word_exchange E  ON E.topic_id=D.topic_id where LOWER(topic_word)='"
                    + word
                    + "' or  word_third='"
                    + word
                    + "' or  word_done='"
                    + word
                    + "' or  word_er='"
                    + word
                    + "' or  word_est='"
                    + word
                    + "' or  word_ing='"
                    + word
                    + "' or  word_pl='"
                    + word + "' or  word_past='" + word + "'";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                dicWord = new DicWord();
                dicWord.setBase_word(c.getString(c.getColumnIndex("topic_word")));
                dicWord.setTopic_id(c.getInt(c.getColumnIndex("topic_id")));
                dicWord.setWord_third(c.getString(c
                        .getColumnIndex("word_third")));
                dicWord.setWord_done(c.getString(c.getColumnIndex("word_done")));
                dicWord.setWord_er(c.getString(c.getColumnIndex("word_er")));
                dicWord.setWord_est(c.getString(c.getColumnIndex("word_est")));
                dicWord.setWord_ing(c.getString(c.getColumnIndex("word_ing")));
                dicWord.setWord_pl(c.getString(c.getColumnIndex("word_pl")));
                dicWord.setWord_past(c.getString(c.getColumnIndex("word_past")));
                dicWord.setCn_mean(c.getString(c.getColumnIndex("mean_cn")));
                return dicWord;
            }
        }
        catch (Exception e)
        {
            log.error(word, e);

        }
        finally
        {
            closeDatabase();
        }
        return dicWord;
    }

    public synchronized static WordExpand findSameAntonym(int topic_id)
    {
        WordExpand wordExpand = null;
        try
        {
            openDatabase();

            String sql = "select topic_id,similar_words,antonym_words,same_analysis FROM SIMILAR_ANTONYM WHERE topic_id="
                    + topic_id;
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                wordExpand = new WordExpand();
                wordExpand.setTopic_id(c.getInt(c.getColumnIndex("topic_id")));
                wordExpand.setAntonym(c.getString(c
                        .getColumnIndex("antonym_words")));
                wordExpand.setSame(c.getString(c
                        .getColumnIndex("similar_words")));
                wordExpand.setSame_analysis(c.getString(c
                        .getColumnIndex("same_analysis")));
                return wordExpand;
            }
        }
        catch (Exception e)
        {
            log.error("topic_id:" + topic_id, e);

        }
        finally
        {
            closeDatabase();
        }
        return wordExpand;
    }

    public synchronized static void initTopics()
    {
        if (cetDicWords.size() > 0)
        {
            return;
        }
        try
        {
            openDatabase();

            String sql = "select * from topic_resource res left join dictionary dict on res.topic=dict.topic_id left join books on books.id=res.book_id LEFT JOIN word_exchange ex on ex.topic_id=res.topic order by book_id desc";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                // word_third word_done word_pl word_ing word_past word_er
                // word_est
                DicWord dicWord = new DicWord();
                dicWord.setBase_word(c.getString(c.getColumnIndex("topic_word")));
                dicWord.setTopic_id(c.getInt(c.getColumnIndex("topic_id")));
                dicWord.setWord_third(c.getString(c
                        .getColumnIndex("word_third")));
                dicWord.setWord_done(c.getString(c.getColumnIndex("word_done")));
                dicWord.setWord_er(c.getString(c.getColumnIndex("word_er")));
                dicWord.setWord_est(c.getString(c.getColumnIndex("word_est")));
                dicWord.setWord_ing(c.getString(c.getColumnIndex("word_ing")));
                dicWord.setWord_pl(c.getString(c.getColumnIndex("word_pl")));
                dicWord.setWord_past(c.getString(c.getColumnIndex("word_past")));
                dicWord.setCn_mean(c.getString(c.getColumnIndex("mean_cn")));
                dicWord.setBook_name(c.getString(c.getColumnIndex("name")));

                cetDicWords.add(dicWord);
                c.moveToNext();
            }
            // topics.remove("target");
            // topics.remove("guy");
            // topics.remove("blank");
            // topics.remove("span");
        }
        catch (Exception e)
        {
            log.error("", e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public static List<Topic> findCETWords(String dialog)
    {
        List<Topic> finds = new ArrayList<Topic>();
        wordAndChars = WordSplit.getUniqueWords(dialog);
        for (String word : wordAndChars)
        {
            for (DicWord dw : cetDicWords)
            {
                final Topic topic = new Topic();
                topic.setTopic_id(dw.getTopic_id());
                topic.setTopic_base_word(dw.getBase_word());
                topic.setMean_cn(dw.getCn_mean());
                topic.setBookName(dw.getBook_name());

                if (hasFind(finds, word, dw.getBase_word(), topic, "base"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_er(), topic, "word_er"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_est(), topic,
                        "word_est"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_done(), topic,
                        "word_done"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_ing(), topic,
                        "word_ing"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_past(), topic,
                        "word_past"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_pl(), topic, "word_pl"))
                {
                    break;
                }
                else if (hasFind(finds, word, dw.getWord_third(), topic,
                        "word_third"))
                {
                    break;
                }
            }
        }
        return finds;
    }

    private static boolean hasFind(List<Topic> finds, String word,
            String dwStr, final Topic topic, String desc)
    {
        if (dwStr != null && dwStr.equalsIgnoreCase(word))
        {
            topic.setMatched_word(word);
            topic.setState(desc);
            if (!finds.contains(topic))
            {
                finds.add(topic);
            }
            return true;
        }
        return false;
    }
}
