# ACGPicDownload

A convenient tool to download ACG pictures from various sources.

## Attention

This project is still **Not stable**, so it may not work well for now...

## Features

- Supports custom sources
- Highly customizable naming rules and fetching urls

## Usage

### Command line arguments

|             Argument              |                                                                                          Description                                                                                           |
| :-------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|          --list-sources           |                                                                                      List all the sources                                                                                      |
|    - -s, --source source_name     |                                                                                Set the source to use. Required.                                                                                |
|  -o, --output output_dictionary   |                                                                              Set the output dictionary. Required.                                                                              |
| --arg key1=value1,key2=value2,... | custom the argument in the url. For example, If the url is `https://www.someurl.com/pic?num=${num}`, then with `-- arg num=1`, The actual address would be `https://www.someurl.com/pic?num=1` |

### Add custom sources

There are already some sources in the default `sources.json`. You can see them to add your own source.

An available source should contain the following values in `sources.json`:

|     Key     |  Type   |                  Description                   |                               Detail                                |
| :---------: | :-----: | :--------------------------------------------: | :-----------------------------------------------------------------: |
|    name     | String  |               Name of the source               | **Required**. Please make sure that each source has different names |
| description | String  |           Description of the source            |                              Optional                               |
|     url     | String  |             The url used to fetch              |                            **Required**                             |
| defaultArgs |  JSON   | The default values of the variables in the url |                 **Required** when using var in url                  |
|  sourceKey  | String  |     The path to image data(s) in the JSON      |                            **Required**                             |
|   picUrl    | String  |    The path to image url in each image data    |                            **Required**                             |
|   asArray   | boolean |    Whether the return value is as an array     |                            **Required**                             |
|  nameRule   | String  |                The naming rules                |       It tells the program how to name the downloaded images        |

#### url

You can add custom vars in the url with `${varname}`. But You need to give a default value for the them
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

#### asArray

if the return value is an array, then you should set it to `true`.

For example the json in the `sourceKey` is returned as an array. So its `asArray` should be `true`.

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

