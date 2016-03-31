# ImageDownloader
## Description

**ImageDownloader** is easy and fast image downloader.
The latest version of **ImageDownloader** is supported for [E-hentai.org](http://e-hentai.org/) and [nhentai.net](http://nhentai.net/).

## Requirement
- Java 1.8 or later
- Apache Commons CLI

## Usage
- Set the following arguments

  `usage: main.ImageDownloader -d <dir> [-f <file>] [-h] [-j] [-u <url>]`

  `-d <dir>    Distination directory`
 
  `-f <file>   File containing target URLs`
 
  `-h          Print help`
 
  `-j          Make directory with Japanese name`
 
  `-u <url>    Target URL`

 The argument `-d` is required. 
 One of `-f` and `-u` is also required.

- Then, **ImageDownloader** collects images in the target URL into the distination directory.
**ImageDownloader** automatically decides [E-hentai.org](http://e-hentai.org/) or [nhentai.net](http://nhentai.net/) as the download website from the target URL.
In the case of setting the argument `-f`, each URL in the file is targeted for download.

## Todo
- Multi-threading