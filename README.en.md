[中文文档](https://github.com/zxzxy/ACGPicDownload/blob/master/README.md) | English Document

------------------

# ACGPicDownload

A convenient tool to download ACG pictures from various sources.

# Attention

This project is still **Not stable**, so it may not work well for now...

# Features

- Easy to use ([Simple tutorial](#simple-tutorial))
- Supports custom sources
- Supports scheduled execute
- Highly customizable naming rules and fetching urls

# Usage

## Command line arguments

### Subcommand fetch

Download picture from specific source.

#### Usage

```shell
java -jar ACGPicDownload.jar fetch [参数]
```

#### Arguments

|             Argument              |                                                   Description                                                    |
| :-------------------------------: | :--------------------------------------------------------------------------------------------------------------: |
|          --list-sources           |                                               List all the sources                                               |
|     -s, --source source_name      |        Set the source to use. If it's not set, then the program will use the first source in the config.         |
|  -o, --output output_dictionary   |         Set the output dictionary. If it's not set, the program will use the dictionary of the program.          |
| --arg key1=value1,key2=value2,... |                              custom the argument in the url. Please see[url](#url)                               |
|      -m, --max-thread count       | The the max count of download progress at the same time. If count is not given then the count will be unlimited. |
|         -t, --times times         |                         Set times to fetch(**Not Recommended**, may trigger API limits)                          |
|             -f,--full             |        Download and save the image's json file to the json file that has the same name as the image file         |
|     -p, --proxy address:port      |                                    Set the proxy of fetching and downloading                                     |

### Subcommand schedule

Schedule commands.

#### Usage

- Enter `schedule` mode with initial command

  ```shell
  >java -jar ACGPicDownload.jar schedule [arguments] [fetch command to run]
  Schedule>
  ```

- Only enter `schedule` mode

  ```shell
  >java -jar ACGPicDownload.jar schedule
  Schedule>
  ```

In `schedule` mode, you can use the commands below:

| Command                    |              Description               |
| -------------------------- | :------------------------------------: |
| add \<argument> \<command> |            Add fetch event             |
| del \<EventID>             |             Delete a event             |
| list                       |            List all events             |
| start                      | Exit `schedule` mode and start running |

Arguments in `schedule` mode

|               Argument               |                         Description                          |
| :----------------------------------: | :----------------------------------------------------------: |
|      --interval,-i \<interval\>      | Set the interval between running, for example `10s` and `2m` |
| --max-times, -m \<max times to run\> |                   Set the max time to run                    |

## <span id="simple-tutorial">...to be more specific?</span>

### Run directly

You can run ACGPicDownload.jar directly, in this case, program will use default `Lolicon` source...

```shell
>java -jar ACGPicDownload.jar

[Fetch] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=1 ...
[Fetch] Got 1 pictures!
[Fetch] Downloading (FileName) to (OutputDir) from (Link) ...
```

When the program is done, you should be able to find the picture under the program's folder.

### <span id="add-argument">Add arguments</span>

If you want to add some customize argument, then you can follow these steps...

1. First, use `--list-sources` to see all the sources...

    ```shell
    >java -jar ACGPicDownload.jar --list-sources

    [Fetch] Name     |  Description                                 |  URL
    [Fetch] lolicon  |  Picture from Lolicon API (api.lolicon.app)  |  https://api.lolicon.app/setu/v2?{r18=$r18}{&num=$num}{&keyword=$keyword}{&tag=$tag}
    [Fetch] dmoe     |  Picture from Dmoe API (dmoe.cc)             |  https://www.dmoe.cc/random.php
    ```

   ...As you can see, there's two sources available.

   > If it's the first time running, or you haven't config `sources.json` yet, then the program will copy default `sources.json` to its path.

2. Add custom arguments

    1. First, choose a source

       We'll use `lolicon` source in this case.

    2. Custom arguments

       In the `lolicon` source, the `{num=$num}` and other variable is arguments. As for them, you can see the source's page
       to know more. We are setting `num` to 5 in this case.
       What's more, we want the program to download to `pic` folder ...

    3. Run

       According to our choices, the source is `lolicon`, `num` is 5, download to `pic` folder, the arguments should be
       like:`-s lolicon`,`-o pic`,`--arg num=5`
       So, we need to run like this:

         ```shell
         >java -jar ACGPicDownload.jar -s lolicon -o pic --arg num=5

         [Fetch] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=5 ...
         [Fetch] Got 5 pictures!
         [Fetch] Downloading (FileName1) to (OutputDir) from (Link1) ...
         [Fetch] Downloading (FileName2) to (OutputDir) from (Link2) ...
         [Fetch] Downloading (FileName3) to (OutputDir) from (Link3) ...
         [Fetch] Downloading (FileName4) to (OutputDir) from (Link4) ...
         [Fetch] Downloading (FileName5) to (OutputDir) from (Link5) ...
         ```

       When it's done, you should be able to see 5 images under the `pic` folder.

### Schedule command

1. If you want to schedule command, then please see [`Add arguments`](#add-argument) to get your command first.

2. Next, enter `schedule` mode

    ```shell
    >java -jar ACGPicDownload.jar schedule
    Schedule>
    ```

3. If we want to run our command every 10m, run 20 times in total, then we should add `-i 10m` and `-m 20` . So, our
   event should be like `-i 10m -m 20 -s lolicon -o pic --arg num=5`

4. Last add `add` in front of the event to add a event, then use `start` to run.

  ```shell
  >java -jar ACGPicDownload.jar schedule
  Schedule>add -i 10m -m 20 -s lolicon -o pic --arg num=5
  [Schedule] Event added. ID = 0
  Schedule>start
  [Schedule|0|114514] Fetching pictures from https://api.lolicon.app/setu/v2?r18=0&num=5 ...
  [Schedule|0|114514] Got 5 pictures!
  [Schedule|0|114514] Downloading (FileName1) to (OutpuDir) from (Link1) ...
  [Schedule|0|114514] Downloading (FileName2) to (OutpuDir) from (Link2) ...
  [Schedule|0|114514] Downloading (FileName3) to (OutpuDir) from (Link3) ...
  [Schedule|0|114514] Downloading (FileName4) to (OutpuDir) from (Link4) ...
  [Schedule|0|114514] Downloading (FileName5) to (OutpuDir) from (Link5) ...
  [Schedule|0|114514] [Event end]
  ```

## Add custom sources

There are already some sources in the default `sources.json`. You can see them to add your own source.

An available source should contain the following values in `sources.json`:

|             Key             |  Type  |                  Description                   |                                       Detail                                       |
| :-------------------------: | :----: | :--------------------------------------------: | :--------------------------------------------------------------------------------: |
|            name             | String |               Name of the source               |        **Required**. Please make sure that each source has different names.        |
|         description         | String |           Description of the source            |                                      Optional                                      |
|  [returnType](#returnType)  | String |           The return type of the url           | `json` or `redirect`, if it's empty, then the program will choose it automatically |
|         [url](#url)         | String |             The url used to fetch              |                                    **Required**                                    |
| [defaultArgs](#defaultArgs) |  JSON  | The default values of the variables in the url |                                      Optional                                      |
|   [sourceKey](#sourceKey)   | String |     The path to image data(s) in the JSON      |     Optional, if it's empty, the program will try to parse the json directly.      |
|      [picUrl](#picUrl)      | String |    The path to image url in each image data    |                                    **Required**                                    |
|    [nameRule](#nameRule)    | String |                The naming rules                |               It tells the program how to name the downloaded images               |

### <span id="url">url</a>

You can add custom vars in the url with `{sometext=$varname}`, and they are called `variable block`. And you can give them a default value using `defaultArgs`.
If the program can't find the the value of the variable, it will delete the whole variable block in the url.
For example, if the `url` is `https://someurl/pic?{num=$num}{&abc=$arg}` , then with the `--arg num=1` argument, the actual url
will be `https://someurl/pic?num=1`

### <span id="defaultArgs">defaultArgs</a>

You can define default values when using vars in the url. In the example in [`url`](#url), the `defaultArgs` can be:

```json
{
  "defaultArgs": {
    "num": 1
  }
}
```

### <span id="nameRule">nameRule</a>

You can use `{sometext$varname}` to use values from the return JSON as a part of the file name.

For example, if a JSON return result is :

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

Then if the `nameRule` is `{ID:$id }{$title}{ by $author}{.$ext}`, the name of this result will
be `ID:6969 some_title by some_author.png`

If the `nameRule` is empty, the program will try to get the file name from the download link.

### <span id="sourceKey">sourceKey</a>

Because of some source does not return the JSON directly of the image data, for example:

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

The `sourceKey` of the JSON above should be `images/data`

### <span id="picUrl">picUrl</a>

Just like the `sourceKey`, the url of each return value should be told.

For example, the `sourceKey` of the following json should be `urls/original`

```json
{
  "urls": {
    "original": "....."
  },
  "id": 6969,
  "title": "some_title"
}
```

### <span id="returnType">returnType</a>

Different sources may have different return type, we only supports json and redirect for now.

> If you open the `url` in your source directly and gets some texts, then the `returnType` should be `json`
>
> If you get the image directly, then it should be `redirect`
