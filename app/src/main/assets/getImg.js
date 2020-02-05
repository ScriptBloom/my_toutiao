<script>
    var imgList = "";
    var imgs = document.getElementsByTagName("img");
    for(var i=0;i<imgs.length;i++){
        var img = imgs[i];
        imgList = imgList + img.src + ";";
        img.onclick = function(){
            window.bytedance.openImg(this.src);
        }
    }
    window.bytedance.getImgArray(imgList);
</script>