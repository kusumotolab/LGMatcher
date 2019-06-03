# LGMatcher
LGMatcher is Line-based GumTree Matcher.  
It is an implementation of GumTree's Matcher.

Please download the JAR file from [release](https://github.com/kusumotolab/LGMatcher/releases).

## How to Use
Please import LGMatcher to your project with GumTree.

```java
// Create LGMatcher
Matcher matcher = LGMatcher.create(srcContent, dstContent, srcTree, dstTree, mappings);
```

## References
- [paper](https://sdl.ist.osaka-u.ac.jp/pman/pman3.cgi?DOWNLOAD=466)
- [sample project](./example/LGMatcherSample)
