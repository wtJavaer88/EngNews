package zhibo8.api;

import java.util.List;

public class Test
{
    // @Test
    public void da() throws Exception
    {
        List<Zb8News> zuqiuNewsByDay = new NewsExtract()
                .getZuqiuNewsByDay("2017-01-03");
        for (Zb8News zb8News : zuqiuNewsByDay)
        {
            if (zb8News.getChs_content() != null
                    && zb8News.getEng_content() != null)
            {
                System.out.println(zb8News.getTitle() + " / "
                        + zb8News.getFrom_url());
            }
        }
    }

    // @Test
    public void m() throws Exception
    {
        List<Zb8News> zuqiuNewsByYearMonth = new NewsExtract()
                .getNewsByYearMonth(2017, 1, SportType.Zuqiu);
        List<Zb8News> filterTeam = NewsFilter.filterTeam(
                NewsFilter.filterTranslated(zuqiuNewsByYearMonth), "阿森纳");
        for (Zb8News zb8News : filterTeam)
        {
            System.out.println(zb8News.getTitle() + " / "
                    + zb8News.getFrom_url());
        }
    }

    // @Test
    public void s() throws Exception
    {
        List<Zb8News> zuqiuNewsAfterDay = new NewsExtract().getNewsAfterDay(
                "2017-01-01", SportType.Zuqiu);
        for (Zb8News zb8News : zuqiuNewsAfterDay)
        {
            System.out.println(zb8News.getTitle() + " / " + zb8News.getDay());
        }
    }

    // @Test
    public void dd() throws Exception
    {
        new NewsExtract().getNewsBetweenTwoDays("2016-12-29", "2017-01-02",
                SportType.Zuqiu);
    }

    // @Test
    public void dw() throws Exception
    {
        new NewsExtract().getNewsAfterDay("2016-12-29", SportType.Zuqiu);
    }
}
