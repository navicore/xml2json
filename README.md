xml2json - a CLI to turn XML into JSON
-----

Works in two modes.

1. Send in a single XML document via STDIN and get a single json line via STDOUT.
1. Send in a stream of XML documents - 1 whole document per line (you must strip newlines beforehand) via STDIN and get a stream of json lines (jsonl) via STDOUT.

## INSTALL

Presumes 
  * you have `$HOME/bin` in your `PATH`
  * `sbt` is installed
  * `java 1.8+` is installed

```console
sbt clone git@github.com:navicore/xml2json.git
cd xml2json
./install.sh 
<open a new shell instance>
```

## USAGE

#### for help
```console
xml2json -h
```

