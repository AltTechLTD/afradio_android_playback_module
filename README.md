# AF Radio Android Playback plugin

The library helps you integrate the playback into you andrdoid app. 

Installation

1. Import the `afr` library module into your project

2. In the oncreate setion of your `MainActivity` or whatever cativity you want to open you module, create you configuration Parameters using credentials supplied by AF Radio.
```java
    Config config = new Config();
    config.setAppId("tKvHWTnKAXLfxjAYm");
    config.setResId("NFR3cG4sEqLT5pnk3");
```

3. Start the Playback fragment
```java
getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.main, PlaybackFragment.Companion.newInstance(config))
        .commit()
```
4. Done. 