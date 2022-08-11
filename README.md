# MCPE Inventory Animator Tool
Create Animated Inventories from Gif Files (Bedrock Edition only)
![example](https://user-images.githubusercontent.com/63020914/184106224-eddd3236-c07c-4de4-9d8b-020231f2077d.gif)
# Tutorial Video
placeholder link for now
# The Limits
It is important to understand that Bedrock Edition's UI Engine can only do so much at once. Gifs can have a max of 40 frames before looping back. Even if your input gif is longer than 40 frames once it hits 40 frames it loops back to the start. 
<br>
Another limit is the gif aspect ratio. All gifs are scaled to 352 x 332. If I were to double this to 704 x 664 then the max frame count would only be 20. This is due to the inventory pack using a flip book texture, so more frames = larger flip book texture. If we were to try and get more than 40 frames and/or a larger aspect ratio then the flipbook texture would be a lot larger and harder for MCPE's UI Engine to load, causing crashes and terrible performance.
# Possible Program Errors
If you get the error message for "4096 out of bounds" or "I/O error reading image" that means the gif you input is to intensive for ImageIO to read. I reccomend scaling down and optimizing gif files with tools such as https://ezgif.com/
<br>
<br>
Highly reccomend using square aspect ratio gifs for best visual results. Portrait or super stretched landscape images might come out as super low quality or zoomed in.
