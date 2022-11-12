# ACGPicDownload

A convenient tool to download ACG pictures from various sources.

## Attention

This project is still **Work In Progress**, so it may not work well for now...

## Usage

### Command line arguments

- Sources
  - --list-sources : List all the sources

- Fetching
  - -s, --source source_name : Set the source to use. Required.
  - -o, --output output_dictionary : Set the output dictionary. Required.
  - --arg key1=value1,key2=value2,... : custom the argument in the url. For example, If the url is `https://www.someurl.com/pic?num=${num}`, then with `-- arg num=1`, The actual address would be `https://www.someurl.com/pic?num=1`
  
### Add custom sources

An available source should contain the following values:
|     Key     |  Type   |                  Description                   |                                         Detail                                         |
| :---------: | :-----: | :--------------------------------------------: | :------------------------------------------------------------------------------------: |
|    name     | String  |               Name of the source               |          **Required**. Please make sure that each source has different names           |
| description | String  |           Description of the source            |                                       Optional.                                        |
|     url     | String  |             The url used to fetch              |                                      **Required**                                      |
| defaultArgs |  JSON   | The default values of the variables in the url |                           **Required** when using var in url                           |
|  sourceKey  | String  |      The path to image datas in the JSON       |                                     **Required**.                                      |
|   picUrl    | String  |    The path to image url in each image data    |                                     **Required**.                                      |
|   asArray   | boolean |    Whether the return value is as an array     |                                     **Required**.                                      |
|  nameRule   | String  |                The naming rules                | You can use `${varname}` to use values from the return JSON as a part of the file name |  |

#### Notes

##### url

 You can add custom vars in the url with `${varname}`. But You need to give a default value for the them using `defaultArgs`
 For example, if the `url` is `https://someurl/pic?num=${num}` , then with the `--arg num=1` argument, the actual url will be `https://someurl/pic?num=1`
 When using var in `url`, you have to give a default value in `defaultArgs`

##### defaultArgs

 It is required when using vars in the url. In the example in `url`, the `defaultArgs` can be:

`"defaultArgs":{"num" = 1}`