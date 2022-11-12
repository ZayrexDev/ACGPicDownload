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
   - --arg key1=value1,key2=value2,... : custom the argument in the url.
      Example:If the url is `https://www.someurl.com/pic?num=${num}`, then with `-- arg num=1`, The actual address would be `https://www.someurl.com/pic?num=1`
      
