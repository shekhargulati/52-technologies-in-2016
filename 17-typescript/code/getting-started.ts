// Basic Types
var votes = 10;
votes++;
console.log(votes);

var visible: boolean = true;
let storyExists:boolean = false;

let title: string = "Learning TypeScript";
let description: string = 'Learning TypeScript Today!'

// String literals
let fullname = "Shekhar Gulati"
let summary = `${fullname} is ${title} this weekend.`

// Arrays 

var tags: string[] = ["javascript","programming"]
tags.push("typescript")
tags.forEach(function(tag){
    console.log(`Tag ${tag}`)
});

let storyLikedBy: Array<number> = [1,2,3]

enum StoryType {Video, Article, Tutorial}
let st:StoryType = StoryType.Article

let storyTitles = ["Learning TypeScript", "Getting started with TypeScript","Building your first app with TypeScript"]
let titlesAndLengths : [string, number][] = storyTitles.map(function(title){
    let tuple: [string, number]  = [title, title.length]
    return tuple
})

var dontKnow: any = {}
dontKnow = "abc"
dontKnow = 1

var stringOrNumber: (string|number) = 1
stringOrNumber = "hello"


var stories: [string, string[]][] = []

function addStory(title: string, tags: string[]): void {
    stories.push([title, tags])  
}

var tags: string[] = ["javascript","programming"]
let tagLengths: number[] = tags.map(tag => tag.length)


let sortByLength1 = function(x:string, y:string): number {
    return x.length - y.length
}

let  sortByLength2 = function(x:string, y:string){
    return x.length - y.length
}  

let  sortByLength3 = (x: string, y: string) :  number => { 
    return x.length - y.length
}

let  sortByLength4 = (x: string, y: string) :  number => { 
    return x.length - y.length
}

let  sortByLength5 = (x: string, y: string) =>  x.length - y.length


let sortByLength6: (x: string, y: string) => number = (x, y) => x.length - y.length
tags.sort(sortByLength6)


function storySummary(title:string, description: string = "") {
    if(description){
        return title + description;
    }else{
        return title;
    }
    
}


interface Story {
    title: string;
    description ?: string;
    tags : string[]
}

let story1:Story = {title:"Learning TypeScript", tags:["typescript","learning"]}

// interface StoryExtractor {
//     extract(url:string): Story
// }

// let extractor:StoryExtractor = {extract: url => story1}

interface StoryExtractor {
    (url:string): Story
}

let extractor:StoryExtractor = url => story1

class TextStory implements Story{
    title:string
    tags: string[]
   
   static storyWithNoTags(title:string): TextStory {
       return new TextStory(title, [])
   } 
    constructor(title:string, ...tags){
        this.title = title;
        this.tags = tags     
    } 
   
   summary (){
       return `TextStory: ${this.title}`
   } 
   
}


let story = TextStory.storyWithNoTags("Learning TypeScript")


class TutorialStory extends TextStory {
    constructor(title:string, ...tags){
        super(title, tags)
    }
    
    summary(){
        return `TutorialStory: ${this.title}`
    }
}

abstract class StoryProcessorTemplate {
    public process(url: string): Story {
        var title: string = this.extractTitle(url)
        var text: string = this.extractText(url)
        var tags: string[] = this.extractTags(text)
        return {
            title : title,
            tags : tags
        }
    }
    
    abstract extractTitle(url:string): string
    
    abstract extractText(url:string): string
    
    abstract extractTags(url:string): string[]
}


module StoryApp{
    export class StoryManager{
        addStory(){}
        removeStory(){}
    }
}

// let manager = new StoryApp.StoryManager()
// manager.addStory()

import s = StoryApp
let manager = new s.StoryManager()
manager.addStory()


interface HasLength{
    length: number
}

function addLengths<T extends HasLength>(t1: T, t2: T):number {
    return t1.length + t2.length;
}

addLengths("hello","abc")
addLengths([1,2,3],[100,11,99])

interface Textable{
    text:string
}

interface Message<T extends Textable>{
    content: T
    
    msg(): string
}

class Pair<T>{
    fst: T
    snd:T
}