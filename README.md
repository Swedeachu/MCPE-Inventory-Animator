# MCPE Inventory Animator Tool
Create Animated Inventories from Gif Files (Bedrock Edition only)

![example](https://user-images.githubusercontent.com/63020914/184106224-eddd3236-c07c-4de4-9d8b-020231f2077d.gif)
![The Tool](https://user-images.githubusercontent.com/63020914/184110154-2e839ac2-200f-4a2f-bd24-f2ca0cb3bffb.PNG)

# Developers:
Swimfan72 (Tool Programmer)                : https://www.youtube.com/c/Swimfan72
<br>
CrisXolt (UI Pack Code + Original Inventor)  : https://www.youtube.com/channel/UCj9Rxb1zbNY1zFXFyATJiJQ
<br>
PolrFlare (UI Pack Code + Other Help) : https://www.youtube.com/channel/UCybsREjkjcOZ_yNl4H7gbFA

# How to use
Drag a gif file into the "select gif file" area. The same can be done for any png if you wish to have an inventory overlay, make sure to have the box checked. Finally, press the large "create animated inventory button". The mcpack will be in the same folder as the Inventory Animator Jar.
<br>
<br>
![rsz_toolio](https://user-images.githubusercontent.com/63020914/184111544-6c17f947-5ab0-4577-830f-7c5842d25e1f.png)

# The Limits
It is important to understand that Bedrock Edition's UI Engine can only do so much at once. Gifs can have a max of 40 frames before looping back. Even if your input gif is longer than 40 frames once it hits 40 frames it loops back to the start. 
<br>
<br>
Another limit is the gif aspect ratio. All gifs are scaled to 352 x 332. If I were to double this to 704 x 664 then the max frame count would only be 20. This is due to the inventory pack using a flip book texture, so more frames = larger flip book texture. If we were to try and get more than 40 frames and/or a larger aspect ratio then the flipbook texture would be a lot larger and harder for MCPE's UI Engine to load, causing crashes and terrible performance.

# Possible Program Errors
If you get the error message for "4096 out of bounds" or "I/O error reading image" that means the gif you input is to intensive for ImageIO to read. I reccomend scaling down and optimizing gif files with tools such as https://ezgif.com/
<br>
<br>
Highly reccomend using square aspect ratio gifs for best visual results. Portrait or super stretched landscape images might come out as super low quality or zoomed in.
