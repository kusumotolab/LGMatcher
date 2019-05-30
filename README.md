# LGMatcher
LGMatcher is Line-based GumTree Matcher.  
It is an implement of GumTree's Matcher.

Please download the JAR file from [release](https://github.com/kusumotolab/LGMatcher/releases).

## Hot to Use
Please import LGMatcher to your project with GumTree.
```java
// Create LGMatcher
Matcher matcher = LGMatcher.create(srcContent, dstContent, srcTree, dstTree, mappings);
```