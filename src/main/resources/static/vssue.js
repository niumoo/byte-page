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