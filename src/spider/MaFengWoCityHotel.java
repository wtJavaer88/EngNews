package spider;

import org.jsoup.Jsoup;

import com.wnc.basic.BasicFileUtil;
import common.uihelper.MyAppParams;
import common.utils.SslUtils;

public class MaFengWoCityHotel
{
	public MaFengWoCityHotel()
	{
	}

	public void grabData()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					SslUtils.trustAllHttpsCertificates();
					String ret = Jsoup
							.connect(
									"https://m.mafengwo.cn/rest/hotel/hotels/?data_style=mobile&filter%5Bmddid%5D=10208&filter%5Barea_id%5D=-1&filter%5Bpoi_id%5D=&filter%5Bdistance%5D=10000&filter%5Bcheck_in%5D=2017-11-21&filter%5Bcheck_out%5D=2017-11-22&filter%5Bprice_min%5D=&filter%5Bprice_max%5D=&filter%5Btag_ids%5D=&filter%5Bsort_type%5D=price&filter%5Bsort_flag%5D=DESC&filter%5Bhas_booking_rooms%5D=0&filter%5Bhas_faved%5D=0&filter%5Bkeyword%5D=&filter%5Bboundary%5D=0&page%5Bmode%5D=sequential&page%5Bboundary%5D=0&page%5Bnum%5D=20")
							.timeout(15000).ignoreContentType(true).execute()
							.body();
					BasicFileUtil
							.writeFileString(MyAppParams.getInstance()
									.getWorkPath() + "city-hotel.txt", ret,
									null, false);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		}).start();
	}
}
