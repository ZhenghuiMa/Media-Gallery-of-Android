# Media-Gallery-of-Android
This is an Android project providing top ten videos from Google Youtube API and News from NPR news. An information system built by MySQL and PHP(with Ajax) is to organize Recommendation data and push notification to the client side.

## Details

The information of videos are stored in SQLite database and updated automatically. User can also add and delete the videos they like in the Favorite List. Moreover, User can also read news via the application in WebView and rate the news by RatingBar. The first part is registration and Log in. The second part is a MixedActivity contains three fragments. They are VideoFragment, NewsFragment, and WishListFragment. User can navigate not only by swiping but also by clicking the tab implemented by ViewPager. Fragment communicate with other fragments via the parent Activity. It is an delegate design pattern.  

1. Using Google Cloud Messaging to implement notification.  
2. Using ListFragment and RecyclerView to enhance ViewPager + Fragments.  
3. Settings and PreferenceFragment.  
4. Sync external database and internal database(Table “recommend”) by timestamp  

![alt tag](http://zhenghuima.info/blog/wp-content/uploads/2016/03/IMG_3440.jpg)
![alt tag](http://zhenghuima.info/blog/wp-content/uploads/2016/03/IMG_3438.jpg)
![alt tag](http://zhenghuima.info/blog/wp-content/uploads/2016/03/IMG_3439.jpg)
![alt tag](http://zhenghuima.info/blog/wp-content/uploads/2016/02/IMG_3359.jpg)
![alt tag](http://zhenghuima.info/blog/wp-content/uploads/2016/02/IMG_3357.jpg)
