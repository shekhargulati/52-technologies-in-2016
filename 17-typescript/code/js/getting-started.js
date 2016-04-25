var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
// Basic Types
var votes = 10;
votes++;
console.log(votes);
var visible = true;
var storyExists = false;
var title = "Learning TypeScript";
var description = 'Learning TypeScript Today!';
// String literals
var fullname = "Shekhar Gulati";
var summary = fullname + " is " + title + " this weekend.";
// Arrays 
var tags = ["javascript", "programming"];
tags.push("typescript");
tags.forEach(function (tag) {
    console.log("Tag " + tag);
});
var storyLikedBy = [1, 2, 3];
var StoryType;
(function (StoryType) {
    StoryType[StoryType["Video"] = 0] = "Video";
    StoryType[StoryType["Article"] = 1] = "Article";
    StoryType[StoryType["Tutorial"] = 2] = "Tutorial";
})(StoryType || (StoryType = {}));
var st = StoryType.Article;
var storyTitles = ["Learning TypeScript", "Getting started with TypeScript", "Building your first app with TypeScript"];
var titlesAndLengths = storyTitles.map(function (title) {
    var tuple = [title, title.length];
    return tuple;
});
var dontKnow = {};
dontKnow = "abc";
dontKnow = 1;
var stringOrNumber = 1;
stringOrNumber = "hello";
var stories = [];
function addStory(title, tags) {
    stories.push([title, tags]);
}
var tags = ["javascript", "programming"];
var tagLengths = tags.map(function (tag) { return tag.length; });
var sortByLength1 = function (x, y) {
    return x.length - y.length;
};
var sortByLength2 = function (x, y) {
    return x.length - y.length;
};
var sortByLength3 = function (x, y) {
    return x.length - y.length;
};
var sortByLength4 = function (x, y) {
    return x.length - y.length;
};
var sortByLength5 = function (x, y) { return x.length - y.length; };
var sortByLength6 = function (x, y) { return x.length - y.length; };
tags.sort(sortByLength6);
function storySummary(title, description) {
    if (description === void 0) { description = ""; }
    if (description) {
        return title + description;
    }
    else {
        return title;
    }
}
var story1 = { title: "Learning TypeScript", tags: ["typescript", "learning"] };
var extractor = function (url) { return story1; };
var TextStory = (function () {
    function TextStory(title) {
        var tags = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            tags[_i - 1] = arguments[_i];
        }
        this.title = title;
        this.tags = tags;
    }
    TextStory.storyWithNoTags = function (title) {
        return new TextStory(title, []);
    };
    TextStory.prototype.summary = function () {
        return "TextStory: " + this.title;
    };
    return TextStory;
}());
var story = TextStory.storyWithNoTags("Learning TypeScript");
var TutorialStory = (function (_super) {
    __extends(TutorialStory, _super);
    function TutorialStory(title) {
        var tags = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            tags[_i - 1] = arguments[_i];
        }
        _super.call(this, title, tags);
    }
    TutorialStory.prototype.summary = function () {
        return "TutorialStory: " + this.title;
    };
    return TutorialStory;
}(TextStory));
var StoryProcessorTemplate = (function () {
    function StoryProcessorTemplate() {
    }
    StoryProcessorTemplate.prototype.process = function (url) {
        var title = this.extractTitle(url);
        var text = this.extractText(url);
        var tags = this.extractTags(text);
        return {
            title: title,
            tags: tags
        };
    };
    return StoryProcessorTemplate;
}());
var StoryApp;
(function (StoryApp) {
    var StoryManager = (function () {
        function StoryManager() {
        }
        StoryManager.prototype.addStory = function () { };
        StoryManager.prototype.removeStory = function () { };
        return StoryManager;
    }());
    StoryApp.StoryManager = StoryManager;
})(StoryApp || (StoryApp = {}));
// let manager = new StoryApp.StoryManager()
// manager.addStory()
var s = StoryApp;
var manager = new s.StoryManager();
manager.addStory();
function addLengths(t1, t2) {
    return t1.length + t2.length;
}
addLengths("hello", "abc");
addLengths([1, 2, 3], [100, 11, 99]);
var Pair = (function () {
    function Pair() {
    }
    return Pair;
}());
