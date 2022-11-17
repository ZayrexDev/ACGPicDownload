中文文档 | [English Document](https://github.com/zxzxy/ACGPicDownload/blob/master/README.en.md)

--------------------

# ACGPicDownload

一个从不同下载源下载 ~~二次元涩图~~ 图片的工具

# 注意

这个项目仍然处于**不稳定**状态，所以可能会出现一些蜜汁小bug qwq

# 特性

- 简单易用 ([简明教程](#tutorial))
- 支持自定义下载源 ([添加自定义下载源](#添加自定义下载源))
- 支持定时执行 ([子指令 schedule](#schedule))
- 高度可自定义的下载连接与文件名

![2022-11-17 14-51-29_2](https://user-images.githubusercontent.com/73475219/202380257-1592f3fb-e33a-49e2-be63-dca53291c4fd.gif)

# 如何使用

## 命令行参数

### 子指令 fetch

从指定的下载源下载图片。

- 用法:

  ```shell
  java -jar ACGPicDownload.jar fetch [参数]
  ```
- 参数列表:

  |                参数名                |                                                                    描述                                                                     |
  |:---------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------:|
  |          --list-sources           |                                                                列出所有已配置的下载源                                                                |
  |     -s, --source source_name      |                                                         设置需要使用的源，若为空，使用配置下的第一个下载源                                                         |
  |  -o, --output output_dictionary   |                                                            设置下载目录，若为空，则默认为当前目录                                                            |
  | --arg key1=value1,key2=value2,... | 自定义URL里的某些参数。 详细请参见[url](#url) |
  |          --multi-thread           |                                          **实验性功能** 启用多线程下载 可能会提升下载速度?                                        |

### <span id="schedule">子指令 schedule</span>

定时执行指令

- 用法

  - 添加初始指令并进入`schedule`模式

    ```shell
    >java -jar ACGPicDownload.jar schedule [参数] [要执行的fetch指令]
    ```

  - 仅进入`schedule`模式

    ```shell
    >java -jar ACGPicDownload.jar schedule
    Schedule>
    ```

  在`schedule`模式模式下，你可以使用以下指令:

  |指令|描述|
  |:-------:|:------:|
  |add \<参数\> \<指令\>|添加fetch事件|
  |del \<指令id\>|删除某一条事件|
  |list|列出目前所有添加的事件|
  |start|退出`schedule`模式并开始执行事件|

  - 参数列表

  |参数名|描述|
  |:----:|:----:|
  | --interval,-i \<时间间隔\> | 指定每次执行的间隔，例如`10s`,`2m` |
  | --max-times, -m \<最大次数\>|指定执行的最大次数|

## <span id="tutorial">...说得再具体些？

### 直接运行

你可以直接使用命令行运行 ACGPicDownload.jar ，这会使用默认的 `lolicon` 源...

```
>java -jar ACGPicDownload.jar

[Fetch] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=1 ...
[Fetch] Got 1 pictures!
[Fetch] Downloading (文件名) to (保存目录) from (下载连接) ...
```

当运行完成后，你就可以在程序目录下找到下载完成的图片。

### 添加参数

1. 首先，使用 `--list-sources` 查看可用的下载源...

    ```shell
    >java -jar ACGPicDownload.jar --list-sources

    [Fetch] Name     |  Description                                 |  URL
    [Fetch] lolicon  |  Picture from Lolicon API (api.lolicon.app)  |  https://api.lolicon.app/setu/v2?{r18=$r18}{&num=$num}{&keyword=$keyword}{&tag=$tag}
    [Fetch] dmoe     |  Picture from Dmoe API (dmoe.cc)             |  https://www.dmoe.cc/random.php
    ```

   ...可以看到，目前默认有两个下载源可用。

   > 如果你是第一次运行并且没有配置 `sources.json`
   >，那么在执行一次与下载源有关的操作时，程序将会自动复制默认的 `sources.json` 到程序目录。

2. 设置自定义参数

    1. 首先，确定下载源

       我们将会在这个例子中使用 `lolicon` 下载源。

    2. 自定义参数

       在 `lolicon` 下载源中，url里面包含的 `{&num=$num}` 等即为参数块。具体配置可见图源的地址。 我们暂且决定将 `num` 设置为 5。
       并且，我们希望程序下载到程序目录下的 `pic` 文件夹...

    3. 运行

       根据我们的选择，图源使用 `lolicon`，将 `num` 设置为 5，下载到程序目录下的 `pic`
       文件夹，则应该分别加上如下的参数:`-s lolicon`,`-o pic`,`--arg num=5`
       那么我们应该这样运行：

         ```shell
         >java -jar ACGPicDownload.jar -s lolicon -o pic --arg num=5

         [Fetch] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=5 ...
         [Fetch] Got 5 pictures!
         [Fetch] Downloading (文件名1) to (保存目录) from (下载连接1) ...
         [Fetch] Downloading (文件名2) to (保存目录) from (下载连接2) ...
         [Fetch] Downloading (文件名3) to (保存目录) from (下载连接3) ...
         [Fetch] Downloading (文件名4) to (保存目录) from (下载连接4) ...
         [Fetch] Downloading (文件名5) to (保存目录) from (下载连接5) ...
         ```

       当程序运行完成后，你将会看到在程序目录下新增了5张新下载的图片~

### 定时执行

1. 如果你需要定时执行那么请先按照 `添加参数` 一节的步骤确定要运行的指令

2. 接下来，先进入 `schedule` 子指令

  ```shell
  >java -jar ACGPicDownload.jar schedule
  Schedule>

  ```

3. 假设我们要将上面的指令每10分钟运行一次，总共运行20次，那么我们应该加上 `-i 10m` 与 `-m 20` 的参数。那么我们的事件指令应为 `-i 10m -m 20 -s lolicon -o pic --arg num=5`。

4. 最后，在前面添加 `add` 以添加事件，并用 `start` 开始运行。

  ```shell
  >java -jar ACGPicDownload.jar schedule
  Schedule>add -i 10m -m 20 -s lolicon -o pic --arg num=5
  [Schedule] Event added. ID = 0
  Schedule>start
  [Schedule|0|114514] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=5 ...
  [Schedule|0|114514] Got 5 pictures!
  [Schedule|0|114514] Downloading (文件名1) to (保存目录) from (下载连接1) ...
  [Schedule|0|114514] Downloading (文件名2) to (保存目录) from (下载连接2) ...
  [Schedule|0|114514] Downloading (文件名3) to (保存目录) from (下载连接3) ...
  [Schedule|0|114514] Downloading (文件名4) to (保存目录) from (下载连接4) ...
  [Schedule|0|114514] Downloading (文件名5) to (保存目录) from (下载连接5) ...
  [Schedule|0|114514] [Event end]
  ```

## 添加自定义下载源

在默认的 `sources.json` 中已有几个配置完成的下载源. 你可以参照他们来创建你的自定义下载源.

一个正常的 `sources.json` 应当包含以下的参数:

|             参数名          | 参数类型 |        描述                       |              补充说明                            |
|:---------------------------:|:-------:|:---------------------------------:|:------------------------------------------------:|
|            name             | 字符串  |      下载源的名称                  |    **需要**. 请确保不同的下载源具有不同的名字     |
|         description         | 字符串  |      下载源的描述                  |               可选                               |
|  [returnType](#returnType)  | 字符串  |     下载源的返回类型               | `json`或`redirect`,当为空时，将会尝试自动判断     |
|         [url](#url)         | 字符串  |       下载URL                      |             **需要**                             |
| [defaultArgs](#defaultArgs) |  JSON   |    URL中参数的默认值               |     可选                                         |
|   [sourceKey](#sourceKey)   | 字符串  |  返回值中指向每张图片数据的路径     |       可选，当为空时，将会直接尝试解析返回值      |
|      [picUrl](#picUrl)      | 字符串  | 每张图片数据中指向每下载链接的路径  |             **需要**                             |
|    [nameRule](#nameRule)    | 字符串  |      下载的命名规则                |               可选                                |

### url

你可以通过形如 `{sometext=$varname}` 的参数块在url中自定义参数，并且你可以使用 `defaultArgs` 为每个参数设定一个默认值。
如果程序没有在某一个参数块中找到变量对应的值，那么就会将整个参数块从url中暂时移除。
举个栗子，如果 `url` 是 `https://someurl/pic?{num=$num}{&keyword=$tag}` ，那么在命令行传入 `--arg num=1`
时，实际上程序访问的url会是 `https://someurl/pic?num=1`

### defaultArgs

当在使用 `url` 中的参数时可以指定。在 [`url`](#url) 的例子中，`defaultArgs` 可以是:

```json
{
  "defaultArgs": {
    "num": 1
  }
}
```

### nameRule

你可以使用形如 `${变量名}` 来使用返回值的json里的值来为每个文件命名

例如，如果一个json返回值是 :

```json
{
  "ext": "png",
  "urls": {
    "original": "....."
  },
  "author": "some_author",
  "id": 6969,
  "title": "some_title"
}
```

那么如果 `nameRule` 是 `{ID:$id }{$title}{ by $author}.{$ext}`,
下载下来的文件名就会是 `ID:6969 some_title by some_author.png`。

如果 `nameRule` 是空的, 程序将会尝试从返回的下载链接自动获取文件名。

### sourceKey

因为某些返回值是由某些值嵌套图片数据的，所以需要指定图片数据的位置。例如:

```json
{
  "images": {
    "data": [
      {
        "ext": "png",
        "urls": {
          "original": "....."
        },
        "author": "some_author",
        "id": 6969,
        "title": "some_title"
      },
      {
        "ext": "png",
        "urls": {
          "original": "....."
        },
        "author": "more_author",
        "id": 1145,
        "title": "more_title"
      }
    ]
  }
}
```

对于以上的返回值 `sourceKey` 应是 `images/data`

### picUrl

就如 `sourceKey`, 在返回值中指向下载连接的路径也应该在此被提前告知。

例如，以下返回值的 `sourceKey` 应该为 `urls/original`。

```json
{
  "urls": {
    "original": "....."
  },
  "id": 6969,
  "title": "some_title"
}
```

### returnType

不同的下载源可能会有不同的返回值类型，目前仅支持 json 返回和重定向。

> 如果你的下载源的 `url` 直接访问会得到一串字符串，那么 `returnType` 就应该是 `json`
>
> 如果直接访问会直接打开图片，那么就是 `redirect`
