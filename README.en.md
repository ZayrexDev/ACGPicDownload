[<- 中文版](https://github.com/zxzxy/ACGPicDownload/blob/master/README.md)

------------------

# ACGPicDownload

A convenient tool to download ACG pictures from various sources.

## Attention

This project is still **Not stable**, so it may not work well for now...

## Features

- Easy to use
- Supports custom sources
- Highly customizable naming rules and fetching urls

## Usage

### Command line arguments

|             Argument              |                                                                                          Description                                                                                           |
|:---------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|          --list-sources           |                                                                                      List all the sources                                                                                      |
|      -s, --source source_name     |                                               Set the source to use. If it's not set, then the program will use the first source in the config.                                                |
|  -o, --output output_dictionary   |                                                Set the output dictionary. If it's not set, the program will use the dictionary of the program.                                                 |
| --arg key1=value1,key2=value2,... | custom the argument in the url. For example, If the url is `https://www.someurl.com/pic?num=${num}`, then with `--arg num=1`, The actual address would be `https://www.someurl.com/pic?num=1`  |
|           --multi-thread          |                                      **Experimental**. Enable multi thread download. <font color=grey>(May improve download speed?)</font>                                                     |

### ...to be more specific?

#### Run directly

You can run ACGPicDownload.jar directly, in this case, program will use default `Lolicon` source...

```shell
>java -jar ACGPicDownload.jar --list-sources

>
```

When the program is done, you should be able to find the picture under the program's floder.

#### Add arguments

If you want to add some customize argument, then you can follow these steps...

1. First, use `--list-sources` to see all the sources...

    ```shell
    >java -jar ACGPicDownload.jar --list-sources
    Name | Description | URL
    Lolicon | Picture from Lolicon API (api.lolicon.app) | https://api.lolicon.app/setu/v2?r18=${r18}&num=${num}
    Dmoe | Picture from Dmoe API (dmoe.cc) | https://www.dmoe.cc/random.php?return=json
    >
    ```

   ...As you can see, there's two sources available.

   > If it's the first time running, or you haven't config `sources.json` yet, then the program will copy default `sources.json` to its path.

2. Add custom arguments

    1. First, choose a source

       We'll use `Lolicon` source in this case.

    2. Custom arguments

       In the `Lolicon` source, the `${num}` and other variable is arguments. As for them, you can see the source's page to know more. We are setting `num` to 5 in this case.
       What's more, we want the program to download to `pic` folder ...

    3. Run

       According to our choices, the source is `Lolicon`, `num` is 5, download to `pic` folder, the arguments should be like:`-s Lolicon`,`-o pic`,`--arg num=5`
       So, we need to run like this:

         ```shell
         >java -jar ACGPicDownload.jar -s Lolicon -o pic --arg num=5
         ```

       When it's done, you should be able to see 5 images under the `pic` folder.

### Add custom sources

There are already some sources in the default `sources.json`. You can see them to add your own source.

An available source should contain the following values in `sources.json`:

|     Key     |  Type  |                  Description                   |                                  Detail                                   |
|:-----------:|:------:|:----------------------------------------------:|:-------------------------------------------------------------------------:|
|    name     | String |               Name of the source               |   **Required**. Please make sure that each source has different names.    |
| description | String |           Description of the source            |                                 Optional                                  |
|     url     | String |             The url used to fetch              |                               **Required**                                |
| defaultArgs |  JSON  | The default values of the variables in the url |                    **Required** when using var in url                     |
|  sourceKey  | String |     The path to image data(s) in the JSON      | Optional, if it's empty, the program will try to parse the json directly. |
|   picUrl    | String |    The path to image url in each image data    |                               **Required**                                |
|  nameRule   | String |                The naming rules                |          It tells the program how to name the downloaded images           |

#### url

You can add custom vars in the url with `${varname}`. But You need to give a default value for them
using `defaultArgs`
For example, if the `url` is `https://someurl/pic?num=${num}` , then with the `--arg num=1` argument, the actual url
will be `https://someurl/pic?num=1`
When using var in `url`, you have to give a default value in `defaultArgs`

#### defaultArgs

It is required when using vars in the url. In the example in `url`, the `defaultArgs` can be:

```json
{
  "defaultArgs": {
    "num": 1
  }
}
```

#### nameRule

You can use `${varname}` to use values from the return JSON as a part of the file name.

For example, if a JSON return result is :

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

Then if the `nameRule` is `ID:${id} ${title} by ${author}.${ext}`, the name of this result will
be `ID:6969 sometitle by someauthor.png`

If the `nameRule` is empty, the program will try to get the file name from the download link.

#### sourceKey

Because of some source does not return the JSON directly of the image datas, for example:

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

The `sourceKey` of the JSON above should be `images/data`

#### picUrl

Just like the `sourceKey`, the url of each return value should be told.

For example, the following json's `sourceKey` should be `urls/original`

```json
{
  "urls": {
    "original": "....."
  },
  "id": 6969,
  "title": "sometitle"
}
```

