// 文字格式化
pangu.spacingElementByClassName('byte-post-content');
document.addEventListener('DOMContentLoaded', () => {
    pangu.autoSpacingPage();
});
// 回到顶部
// Get the button
let mybutton = document.getElementById("myBtn");
// When the user scrolls down 20px from the top of the document, show the button
window.onscroll = function() {scrollFunction()};
function scrollFunction() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        mybutton.style.display = "block";
    } else {
        mybutton.style.display = "none";
    }
}
// When the user clicks on the button, scroll to the top of the document
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}

(function vssueLoad() {
    new Vue({
        el: '#vssue',
        render: h => h('Vssue', {
            props: {
                // 在这里设置当前页面对应的 Issue 标题
                title: document.title,
                // 在这里设置你使用的平台的 OAuth App 配置
                options: {
                    owner: 'niumoo', // 仓库的拥有者的名称
                    repo: 'comment', // 存储 Issue 和评论的仓库的名称
                    clientId: 'f6658cb1a9c19957122d', // 刚保存下来的  Client ID
                    clientSecret: '9640aaf6e2d3558ba19751aeab2d60b776d1cff1', //  刚才保存下来的 Client secrets
                },
            }
        })
    })
})()


// 全屏
function enableFullscreenForImages(divId){const styleElement=document.createElement('style');const styleRules=`.fullscreen-image{position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);max-width:100%;max-height:100%;z-index:1000}.fullscreen-overlay{position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:999}`;styleElement.type='text/css';if(styleElement.styleSheet){styleElement.styleSheet.cssText=styleRules}else{styleElement.appendChild(document.createTextNode(styleRules))}document.getElementsByTagName('head')[0].appendChild(styleElement);const targetDiv=document.getElementById(divId);if(!targetDiv){console.warn(`No div found with id:${divId}`);return}targetDiv.addEventListener('click',function(event){const target=event.target;if(target.tagName==='IMG'){const overlay=document.createElement('div');overlay.className='fullscreen-overlay';const fullscreenImage=target.cloneNode();fullscreenImage.className='fullscreen-image';const removeFullscreen=()=>{fullscreenImage.remove();overlay.remove();document.removeEventListener('keydown',onKeyPress)};overlay.addEventListener('click',removeFullscreen);const onKeyPress=(e)=>{if(e.key==='Escape'){removeFullscreen()}};document.addEventListener('keydown',onKeyPress);document.body.appendChild(overlay);document.body.appendChild(fullscreenImage)}})}

/*

// 图片浮动显示
// 获取需要添加悬浮效果的元素
var hoverElement = document.getElementById('sponsor');
// 创建鼠标移入事件监听器
hoverElement.addEventListener('mouseenter', function() {
    showNotification(hoverElement);
});
// 创建鼠标移出事件监听器
hoverElement.addEventListener('mouseleave', function() {
    // 获取hoverElement下面的<img>元素
    var imgDiv = document.getElementById('notification');
    // 移除<img>元素
    imgDiv.remove();
});

function showNotification(ref) {
    // 创建新的<img>元素
    var node = document.createElement('img');
    // 设置图片URL
    node.src = 'https://cdn.debug.group/git/webinfo/wp.png';
    // 将<img>元素添加到hoverElement下面
    node.className = "notification";
    node.id = "notification";
    let coords = ref.getBoundingClientRect();
    node.style.right = "20px";
    node.style.top = coords.bottom + 5 + "px";
    node.style.width = "250px";
    document.body.appendChild(node);
}*/
