**In recent years**, we have seen a new innovation trend in mobile devices with Foldable phones.  This allows the users to to use dual adaptive screens within just one phone. It helps the users with their daily task with a larger screen and also fits in your pocket comfortably.

The majority of well-known phone manufactures such as Samsung, Xioami have launched foldable devices which are gaining a lot of popularity among the users. Subsequently, Developers need to provide a new user experience with this added advantage for a larger screen. Developers should focus on utilizing this extra space to make a more interactive experience for the user that makes a  difference.

Most people treat  foldable phone like a combination of a tablet and a smartphone. Foldable devices can be in different states like **FLAT**, **HALF_OPENED** (FLEX MODE) and **FOLDED**. This also includes different screen orientations like **`BOOK_MODE`** and **`TABLE_TOP`**. All this combination needs to be considered while developing apps for foldable devices.

[![Foldable Device Orientation](https://developer.android.com/images/guide/topics/large-screens/foldables/foldable_multiple_postures.png "Foldable Device Orientation")](https://developer.android.com/images/guide/topics/large-screens/foldables/foldable_multiple_postures.png "Foldable Device Orientation")


Our app should dynamically adapt the UI changes according to the current state. This is very important to provide a smooth user experience while adapting these changes. In this article, I will guide you through the process to make your app campatible with foldable devcies. Also, You should go through the [Official documentation](https://developer.android.com/guide/topics/large-screens/learn-about-foldables "official documentation") to learn more about these concepts.

#### Phase 1: Make Resizeble Activity

For a better user experience in different states, it is recommended to make your activity resizble from **AndroidManifest** file:

    android:resizeableActivity="true"

#### Phase 2: Update View

To handle different configurations, we will **RxJava** for reactive programming. Firstly, we need to add **RxJava** dependecy to our app gradle file.



    implementation 'androidx.window:window-rxjava2:1.0.0'
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'

We will use **disposable** so we can dispose the stream when necessary. Declare the *Disposable* and *observable* in your activity.



    private lateinit var observable: Observable<WindowLayoutInfo>
    private var disposable: Disposable? = null

Now, we will use **WindowInfoTracker** API which will provide us with necessary information regarding the **WindowLayoutInfo** containing *displayFeatures* required to identify folding state of the device. 

We need to initialize the observable with WindowInfoTracker.

    observable = WindowInfoTracker.getOrCreate(this)
                .windowLayoutInfoObservable(this)

Our disposable will subscribe to the observable to find the layout changes. Lets assumme, we have a Recycler View inside our activity and we want to make changes to the Recycler View when device is in `HALF_OPENED` state. We can follow this code below to make changes according to our needs.



    disposable?.dispose()
            disposable = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { windowLayoutInfo ->
                    windowLayoutInfo.displayFeatures.filterIsInstance(FoldingFeature::class.java)
                        .firstOrNull()?.let { foldingFeature ->
                            if (foldingFeature.state == FoldingFeature.State.HALF_OPENED) {
                                recyclerView.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                            } else {
                                recyclerView.layoutManager = GridLayoutManager(
                                    context,
                                    3,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            }
                }

Lastly, We should follow good practices in our code and dispose the stream *onStop()*.

        override fun onStop() {
        	super.onStop()
        	disposable?.dispose()
         }

We can easily make a foldable app to get started with the functionality. There are many more things to take into consideration for device orientation, hinge. and different states. We can use [ReactiveGuide](http:/https://developer.android.com/training/constraint-layout/foldables "ReactiveGuide") to precisely optimize our app for foldable devices.

#### Phase 3: Testing

Foldable devices offer more complex use case test scenerios. This could be pretty expensive to buy a foldable device for testing. Thankfully, Foldable devices are availabe with  [Android Emulator v30.0.6+](http://https://developer.android.com/studio/releases/emulator#30-0-26 "Android Emulator v30.0.6+") . Also, You can use [Samsung Remote Test Lab](http://https://developer.samsung.com/remote-test-lab "Samsung Remote Test Lab") to test your app on foldable devices.

