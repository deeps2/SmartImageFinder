# SmartImageFinder <img src="app/src/main/res/mipmap-hdpi/ic_launcher.png" />
Image search app using MVVM pattern, Room ORM<br/>
APK: https://drive.google.com/file/d/1CCN1U5mLpY0tt0Ki9FbE6FqNXismlUa_/view?usp=sharing

# ScreenShots

<img src="https://res.cloudinary.com/deeps2/image/upload/v1591584723/rheo_photos/two_col.png" width=280>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="https://res.cloudinary.com/deeps2/image/upload/v1591584723/rheo_photos/four_col.png" width=280/></br></br></br></br>
<img src="https://res.cloudinary.com/deeps2/image/upload/v1566728957/rheo_photos/no_net.png" width=280/>
<img src="https://res.cloudinary.com/deeps2/image/upload/v1566728958/rheo_photos/no_results.png" width=280/>&nbsp;&nbsp; 
<img src="https://res.cloudinary.com/deeps2/w_280/v1591585495/rheo_photos/transition_2.gif"/>

# Features
- Search images by entering the keywork (uses Microsoft Azure Congitive Services Bing Image Search API)
- Supports pagination (fetch results in group of 20)
- Caching: Stores previous search results into SQLite using Room ORM. 
- So, if a search result exists, it can be fetched from local DB. No need to re-hit the API.
- After MAX_ROWS limit is reached, oldest record will be deleted. Otherwise App's cache size will go on increasing.
- API keys are stored natively (JNI) rather than hardcoding it in a constant in java file.
- Grid size can be changed (2 column, 3 column, 4 column)
- **Bonus #1**: Shared Element transition animation when any list item is clicked

# Components Used
- MVVM pattern
- ViewModel, LiveData, Retrofit, Gson, Glide, Butterknife,ConstraintLayout, RecyclerView, CardView
- Room ORM is used for caching of search results
- Uses Bing Image Search API https://docs.microsoft.com/en-us/azure/cognitive-services/bing-image-search/overview
