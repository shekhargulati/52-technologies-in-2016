Building `Read It Later` Mobile application using [Ionic framework](http://ionicframework.com/)
----

The Mobile application development space offers quite a range of development platforms. I am not going to talk about the pros and cons of each of these platforms. Instead we are going to build a `Read It Later` Hybrid app consisting of HTML, JS and CSS using the [Ionic framework](http://ionicframework.com/). The complete application would consist of the following parts :

 - **Server Application** : The server-side component listens to the Twitter API to know any tweets you `liked`. The complete application is built in Python using Tweepy and Newspaper. The list of your `likes` is also exposed via a JSON API.
 - **Mobile Application** : The mobile side component reads the `likes` from the server and presents them in form of simple lists. Each item in the list presents an image, summary text, and a title. The complete list can be updated using the  `Drag Down` feature.

The complete source code for the tutorial is available on [Github](DailyReads/)


----------


### Building `Read It Later` Server
We would re-use the python based [`Read It Later`](https://github.com/shekhargulati/dailyreadsr) developed few weeks back. As stated above the application is built using [Tweepy](https://pypi.python.org/pypi/tweepy), [Newspaper](https://pypi.python.org/pypi/newspaper) and [Flask](https://pypi.python.org/pypi/flask) frameworks. We will take the python app and add a JSON based API to it. In this tutorial, we would only discus the application in brief, please refer to the [original tutorial](https://github.com/shekhargulati/52-technologies-in-2016/tree/master/16-newspaper) for complete details.

The current application has a `LikedTweetsListener` which listens to the steam of tweets using the `tweepy.streaming.StreamListener`.
When it receives a tweet it builds a `newspaper.Article` and extracts all kinds of info from it. The complete info is saved in-memory in an `articles` array which is render via Flask `render_template`.

Now, as a first step we want to send back the the same info in form of JSON.  In order to do so, we have to use `json.dumps` and construct a Response using `flask.Response`. The response should have the `application/json` mime type.

```
@app.route("/api/")
def data():
    return Response(json.dumps(sorted(articles, key=lambda article: article["liked_on"], reverse=True)),  mimetype='application/json')
```

> The list of `articles` built has `text` in form of  bytes which could not be serialized to JSON. Modify the existing `article` to contain only `string`. Also remove the `published` date field as it is not required. Thus the updated `article` has the following structure :
> ```{
        'title':title,
        'img':img,
        'text': text
    }```

Now run the application and access http://localhost:5000/api. It should send back a JSON array.
>`
[{"text": "Dreamweaver is back for us, coders.", "story_url": "https://medium.com/@helloanselm/dreamweaver-is-back-for-us-coders-2a1be75ae595", "title": "Dreamweaver is back for us, coders. \u2014 Medium", "img": "https://cdn-images-1.medium.com/max/1200/1*arIididHfe9iMN4N319XPA.png", "liked_on": 1467978361.62787}]
`

----------

### Building `Read It Later` mobile application
Now lets embark on the journey to build a hybrid mobile application using Ionic. Ionic is based on Angular knowledge of the same is required. The application would be built in ES6 standard using Babel.
Lets start by installing `cordova` and  `ionic`  command line using `npm` :

>npm install -g cordova ionic

The cli gives a host of features to generate, build, run the project. Run the `ionic` command to get the list of tasks. Lets generate a starter Ionic project by running the following command :

>ionic start DailyReadsApp blank

The `blank` signifies the prebuilt template to use. Ionic offers a couple of templates to start e.g. `sidemenu` , `tabs`. Besides these any custom templates can also be used.

The above command will generate a bunch of source files in `DailyReadsApp` folder.

![Project Structure](images/structure.png)

- The `config.xml` contains configuration for the application. It can alter various behaviours of the project.
- The `gulpfile.js`  defines gulp tasks for the project.
- The `www` defines the bundle vixz html, js, css, libs etc which is packaged in the application.
- The `bower.json` and `package.json` define dependencies / dev-dependencies for the project.
- The `plugins` describes the Cordova and ionic plugins being used.
- The `platform` lists the application platforms being built.

Lets go into the `DailyReadsApp` folder and run the application by issuing the following command :
```
cd DailyReadsApp
ionic serve
```
The server would start and would render a page at http://localhost:8100/

![Blank Application](images/blank.png)

The application is now run in live mode. If I make changes to the files inside the `www` folder and it gets refreshed automatically.

The complete application is deployed from the `app.js` file inside the `www\js` directory. Try making some changes to see the effect. The `app.js` contains javascript but I would prefer to use ECMAScript. So lets setup the project to use ES6 and transpile it using [Babel](https://babeljs.io/)

The ES6 can not be used directly so I can not write the same in `app.js`. But I can always write ES6 based code and the ask `gulp` to convert it into javascript using `babel`. Thus to do so, I will create a new directory `src` parallel to `www` folder. Now move the `app.js` from `www\js` to the `src\js` folder. Lets modify the `gulpfile.js` is the following manner :

 Install `gulp-babel` using :
 `npm install gulp-babel --save`

 Now add a `babel` gulp task

```
var babel = require('gulp-babel');
var paths = { es6: ['./src/js/*.js'],  sass: ['./scss/**/*.scss'] };
gulp.task("babel", function () {
  return gulp.src(paths.es6)
    .pipe(babel({presets: ['es2015']}))
    .pipe(gulp.dest("www/js"));
});
```

Lets just add new task to the list of `watch` and `default` tasks as well :

```
gulp.task('default', ['babel','sass']);
gulp.task('watch', function() {
  gulp.watch(paths.es6, ['babel']);
  gulp.watch(paths.sass, ['sass']);
});
```

Now do `gulp` followed by `ionic serve`. It should run rending back the same page. This is all good but writing two commands every time is quite painful. It is important to note the now `ionic` is  no longer listening to file changes to `src` folder and it will not refresh if we make any change there.

>In order to use latest methods like Array.find we need to bundle `babel-polyfill.js` with our application. We can install the same using `bower` : `bower install babel-polyfill --save`. Now include the script in `index.html` :
```
<script src="lib/babel-polyfill/browser-polyfill.js"></script>
```

To fix the above issues lets modify `ionic.project` file. As a first step we should define the correct `name`  of the project. Post that define `gulpDependantTasks` property, the property signified `gulp` tasks which need to executed before a build. So we can say : `gulpDependantTasks : ["babel"]`
Add `gulpStartupTasks` property, it signifies the gulp tasks to keep alive during `ionic server`. Thus we can say `gulpStartupTasks : ["watch"]`

Now do `ionic serve`. It should execute `gulp-babel` first. Try modifying `src/js/app.js` the server should detect the change.

>As a personal preference, I would add `html` and `css` files to the `src` folder and additionally define `gulp` tasks to build them.

The project is now setup correctly, so lets start adding some code.

As a starting point rename the Angular module in `app.js` from 'starter' to `dailyReads`. Also modify `ng-app` attribute of `body` tag in `index.html` to reflect the same.

Any usable app will consist of  multiple views offering different things. Thus we will build an app geared towards multi-views even though we will build a single view. In order to do so remove the `ion-content` tag from `index.html` and replace it with `ion-nav-view`

Also we will replace the `ion-header` with a `ion-nav-bar`. The bar would allow us to have navigation buttons and   headers based on the rendered view.

```
<body ng-app="dailyReads">
  <ion-nav-bar class="bar-positive">
  </ion-nav-bar>
  <ion-nav-view></ion-nav-view>
</body>
```
Lets head back to `src\js\app.js` to construct the view now. On order to do so we need to define a state in Angular.

```
.config(($stateProvider,$urlRouterProvider) => {
  $stateProvider.state("home",{
    cache: false,
    url :'/home',
    controller :'HomeController',
    templateUrl : 'views/home/home.html'
  });

  $urlRouterProvider.otherwise('/home');
})
```
In the above code we defined the `home` state and configured  the route provider to render it.

Now lets build the `HomeController`. All we want to do is to use the `$http` service to call `http://localhost:5000/api/` and then send back the JSON to the page.

```
.controller('HomeController',($scope,$http) => {
    $http.get("http://localhost:5000/api/")
          .success(function(data){
            $scope.news = data;
         }).error(function(data, status, headers, config){
           console.log('oops error occured while refreshing data');
         });
  })
```
All that is left now is to build the HTML page. For now we are going to keep the view quite simple. The news item would be rendered as [ionic cards](http://ionicframework.com/docs/components/#card-images), listing down the title, image and text.
```
<ion-view view-title="Home">
  <ion-content>
    <div class="list card" ng-repeat="newsInfo in news track by $index">
      <div class="item item-body">
         <img src="{{newsInfo.img}}">
         <p>
           {{ newsInfo.text }}
         </p>
       </div>
    </div>
  </<ion-content>  .
</ion-view>
```
The above code is defines the view with a title `Home`.  Next we define the `ion-content`. The directive defines a content area which can be used with scrolling. The rest is a `div` which displays the individual item.

Looks like we are done, so lets start Python server `python app.py` and do a `like` on twitter. The 'http://localhost:5000/' show the favorite. The 'http://localhost:5000/api/' gives back a JSON.

Now run the mobile app using `ionic serve`. It works but does not show the  favorite.Now lets look into `Developer Console`. It lists out an error :
>XMLHttpRequest cannot load http://localhost:5000/api/. No 'Access-Control-Allow-Origin' header is present on the requested resource. Origin 'http://localhost:8100' is therefore not allowed access.

Ok! so looks like we are doing cross origin requests. There is server running on 8100 and another on 5000, so we need to configure proper set of headers to make it working. This needs to be done on the python server side.
Import the `flask-cors` extension using `pip`
```
pip install -g flask-cors
```
Now configure the `flask` application to use the `cors` extension
```
from flask.ext.cors import CORS

app = Flask(__name__)
CORS(app)
```
Run the server and the mobile application. The favorites should be rendered on the server page, JSON api and the mobile application.

The server shows all the `liked` tweets and updates view as soon as on is available. But the mobile app does not update the list. So now we would add the mobile `DragDown` which would update the list of favorites. Ionic provides the `ion-refresher` tag to do the same.
```
<ion-refresher pulling-text="Pull to refresh..." on-refresh="reloadFavs()">
</ion-refresher>
```
The tag needs a method which it would call when dragged. Thus refactor the `HomeController` to have a `reloadFavs` method. The method should specify when to to stop loading indicator. This is done by sending `scroll.refreshComplete` event.
```
$scope.reloadFavs = function(){
  $http.get("http://localhost:5000/api/")
        .success(function(data){
          $scope.news = data;
          $scope.$broadcast('scroll.refreshComplete');
       }).error(function(data, status, headers, config){
         console.log('oops error occured while refreshing data',JSON.stringify(data));
          $scope.$broadcast('scroll.refreshComplete');
       });
}
```
Looks like we are done now !  But wait, we created the application but did not specify platforms(android/ios) for it. By default it would include `ios` platforms. To make a build on the same we would require `Xcode`. List the available platforms using cli :
```
ionic platform list

Installed platforms: ios 3.8.0
Available platforms: amazon-fireos, android, blackberry10, browser, firefoxos, osx, webos
```

Lets now add the `android` platform using the cli :
```
ionic platform add android
```
Now build the `.apk` using `cordova compile android`. This should generate apk file inside the `DailyReadsApp/platforms/android/build/outputs/apk/` folder.

Note, if we install the app to an android device it will not run as it is trying to load data from  `http://localhost:5000/api/`. So make sure to replace the `localhost` with a server IP address. Even after we do the application may not run various devices. This is due to the fact that android platform has a very strict control over what the app can perform. Since our application is trying to make calls to a server we need to whitelist the http calls. This is done by specifying the intent in `config.xml`
```
<allow-intent href="http://*/*" />
```
Our mobile app is now finally done ! So some people might think that we can ship the app now to all kinds of online store like google play. I would say that no, we are still a few steps away before we can accomplish that. We must release the app. The release process would optimize the build and would version it. Post that we need to have credentials for play store. I would leave this for some other time.

The application as of now offers a good test bed to learn and experiment more things like adding splash screens, icons or replacing the pull with a push(via ionic services). I would advice to try out such things to learn more about the framework.  

----------

Please provide your valuable feedback by posting a comment to https://github.com/shekhargulati/52-technologies-in-2016/issues/37
