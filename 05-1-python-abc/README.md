Python abc.py Puzzle
----

Let me start with the confession that I am not an expert Python developer so this might not be a surprise for some of you. Yesterday, I was working on writing a Python REST API client using awesome Python `requests` library for one of my server application. To quickly hack my client, I created a `virtualenv` and installed required libraries using `pip`. I was ready to play with Python(again). I created a new file `abc.py` and added a method. For demonstration, let's suppose our method is called `hello`, as shown below.

```python
def hello(name):
    return "Hello, {0}".format(name)
```

I fired up Python REPL and imported abc.py

```
→ python
Python 2.7.10 (default, Jul 14 2015, 19:46:27)
[GCC 4.2.1 Compatible Apple LLVM 6.0 (clang-600.0.39)] on darwin
Type "help", "copyright", "credits" or "license" for more information.
>>>
>>>
>>> import abc
>>>
```

All is looking great. I can call `hello` method now. I fired the command and greeted with error message.

```
>>> abc.hello("shekhar")
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
AttributeError: 'module' object has no attribute 'hello'
```

To me it was something very basic and should work. I looked up all methods of `abc` using `dir` method. There is no `hello` method.

```
>>> dir(abc)
['ABCMeta', 'WeakSet', '_C', '_InstanceType', '__builtins__', '__doc__', '__file__', '__name__', '__package__', 'abstractmethod', 'abstractproperty', 'types']
```

What's possible is wrong? I couldn't figure out what's wrong with my `abc.py`. After spending an hour on it, I just decided to copy and paste example from [Python documentation for modules](https://docs.python.org/2/tutorial/modules.html). They have an example where a file `fibo.py` contains a method `fib`. When I followed their example, it worked fine. I was able to call `fib` method `fibo.fib`.

So, I decided to rename `abc.py` to `hello.py`. Voila! I can call my `hello` method.

```
>>> import hello
>>> hello.hello("Shekhar")
'Hello, Shekhar'
```

But, what's the issue with name `abc.py`?

I googled `python abc.py` and found out that there `abc.py` in the core Python library https://docs.python.org/2/library/abc.html. My first reactive was who creates a module named `abc.py` in the core library. From the documentation,

> **This module provides the infrastructure for defining abstract base classes (ABCs) in Python, as outlined in PEP 3119; see the PEP for why this was added to Python.**

When we imported our `abc.py` Python basically imported `abc` module of Python core. So, it never imported our code. This is the reason `hello` method was not available on `abc`.
