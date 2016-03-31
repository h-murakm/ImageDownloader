# ImageDownloader
## Description

**ImageDownloader** is easy and fast image downloader.
The latest version of **ImageDownloader** is supported for [E-hentai.org](http://e-hentai.org/) and [nhentai.net](http://nhentai.net/).

## Requirement
- Java 1.8 or later
- Apache Commons CLI

## Usage
1. Set the following arguments

  `main.ImageDownloader [-d <arg>] [-f <arg>] [-u <arg>]`
     `-d <arg>   Distination directory`
     `-f <arg>   File containing target URLs`
     `-u <arg>   Target URL`

2. Then, **ImageDownloader** collects images in the target URL into the distination directory.
**ImageDownloader** automatically decides [E-hentai.org](http://e-hentai.org/) or [nhentai.net](http://nhentai.net/) as the download website from the target URL.
In the case of setting the argument `-f`, each URL in the file is targeted for download.

## Todo
- Multi-threading