[English version ->](https://github.com/zxzxy/ACGPicDownload/blob/master/README.en.md)

--------------------

# ACGPicDownload

一个从不同下载源下载 ~~二次元涩图~~ 图片的工具

## 注意

这个项目仍然处于**不稳定**状态，所以可能会出现一些蜜汁小bug qwq

## 特性

- 简单易用
- 支持自定义下载源
- 高度可自定义的下载连接与文件名

## 如何使用

### 命令行参数

|                参数名                |                                                                     描述                                                                     |
|:---------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------:|
|          --list-sources           |                                                                列出所有已配置的下载源                                                                 |
|    - -s, --source source_name     |                                                         设置需要使用的源，若为空，使用配置下的第一个下载源                                                          |
|  -o, --output output_dictionary   |                                                            设置下载目录，若为空，则默认为当前目录                                                             |
| --arg key1=value1,key2=value2,... | 自定义URL里的某些参数。 例如, 如果URL是 `https://www.someurl.com/pic?num=${num}`, 那么在传入 `-- arg num=1` 后, 实际上访问的地址将会是 `https://www.someurl.com/pic?num=1` |
|         --multi-thread            |                **实验性功能** 启用多线程下载 <font color=grey>（可能会提升下载速度？）</font>                                     |

### ...说得再具体些？

#### 直接运行

你可以直接使用命令行运行 ACGPicDownload.jar ，这会使用默认的`Lolicon`源...

```shell
>java -jar ACGPicDownload.jar --list-sources

>
```

当运行完成后，你就可以在程序目录下找到下载完成的图片。

#### 添加参数

1. 首先，使用 `--list-sources` 查看可用的下载源...

    ```shell
    >java -jar ACGPicDownload.jar --list-sources
    Name | Description | URL
    Lolicon | Picture from Lolicon API (api.lolicon.app) | https://api.lolicon.app/setu/v2?r18=${r18}&num=${num}
    Dmoe | Picture from Dmoe API (dmoe.cc) | https://www.dmoe.cc/random.php?return=json
    >
    ```

    ...可以看到，目前默认有两个下载源可用。

    > 如果你是第一次运行并且没有配置 `sources.json` ，那么在执行一次与下载源有关的操作时，程序将会自动复制默认的 `sources.json` 到程序目录。

2. 设置自定义参数

   1. 首先，确定下载源
   
      我们将会在这个例子中使用 `Lolicon` 下载源。
   
   2. 自定义参数
   
        在 `Lolicon` 下载源中，url里面包含的 `${num}` 等即为参数。具体配置可见图源的地址。 我们暂且决定将 `num` 设置为 5。
        并且，我们希望程序下载到程序目录下的 `pic` 文件夹...
   
   3. 运行
        
        根据我们的选择，图源使用 `Lolicon`，将 `num` 设置为 5，下载到程序目录下的 `pic` 文件夹，则应该分别加上如下的参数:`-s Lolicon`,`-o pic`,`--arg num=5`
        那么我们应该这样运行：
   
        ```shell
        >java -jar ACGPicDownload.jar -s Lolicon -o pic --arg num=5
        ```

        当程序运行完成后，你将会看到在程序目录下新增了5张新下载的图片~

### 添加自定义下载源

在默认的 `sources.json` 中已有几个配置好的下载源. 你可以参照他们来创建你的自定义下载源.

一个正常的 `sources.json` 应当包含以下的参数:

|     参数名     | 参数类型 |        描述         |           补充说明           |
|:-----------:|:----:|:-----------------:|:------------------------:|
|    name     | 字符串  |      下载源的名称       | **需要**. 请确保不同的下载源具有不同的名字 |
| description | 字符串  |      下载源的描述       |            可选            |
|     url     | 字符串  |       下载URL       |          **需要**          |
| defaultArgs | JSON |    URL中参数的默认值     |  **需要**(仅当在`url`中设置参数时)  |
|  sourceKey  | 字符串  |  返回值中指向每张图片数据的路径  |   可选，当为空时，将会直接尝试解析返回值    |
|   picUrl    | 字符串  | 每张图片数据中指向每下载链接的路径 |          **需要**          |
|  nameRule   | 字符串  |      下载的命名规则      |            可选            |

#### url

你可以通过 `${varname}` 在url中自定义参数，但是你需要使用 `defaultArgs` 为每个参数设定一个默认值，
举个栗子，如果 `url` 是 `https://someurl/pic?num=${num}` ，那么在命令行传入 `--arg num=1` 时，实际上程序访问的url会是 `https://someurl/pic?num=1`

#### defaultArgs

当在使用 `url` 中的参数时需要指定。在 `url` 的例子中，`defaultArgs` 可以是:

```json
{
  "defaultArgs": {
    "num": 1
  }
}
```

#### nameRule

你可以使用形如 `${变量名}` 来使用返回值的json里的值来为每个文件命名

例如，如果一个json返回值是 :

```json
{
  "ext": "png",
  "urls": {
    "original": "....."
  },
  "author": "someauthor",
  "id": 6969,
  "title": "sometitle"
}
```

那么如果 `nameRule` 是 `ID:${id} ${title} by ${author}.${ext}`, 下载下来的文件名就会是 `ID:6969 sometitle by someauthor.png`。

如果 `nameRule` 是空的, 程序将会尝试从返回的下载链接自动获取文件名。

#### sourceKey

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
        "author": "someauthor",
        "id": 6969,
        "title": "sometitle"
      },
      {
        "ext": "png",
        "urls": {
          "original": "....."
        },
        "author": "someauthor",
        "id": 1145,
        "title": "sometitle"
      }
    ]
  }
}
```

对于以上的返回值 `sourceKey` 应是 `images/data`

#### picUrl

就如 `sourceKey`, 在返回值中指向下载连接的路径也应该在此被提前告知。

例如，以下返回值的 `sourceKey` 应该为 `urls/original`。

```json
{
  "urls": {
    "original": "....."
  },
  "id": 6969,
  "title": "sometitle"
}
```
