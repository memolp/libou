### JSW模板语言的设计目标

#### 介绍说明
1. 通过调用`echo`将里面的内容显示到页面中。
```html
<!-- index.html -->
<!--模板语法：-->
<h2>{% echo("你好") %}</h2> 
<!--客户端HTML：-->
<h2>你好</h2>
```
2. 模板脚本生效的区域包裹在{%  %} 之中, 可以出现在HTML中的任何位置.
```html
<!-- index.html -->
<label style="{% echo('width:20px') %}"> Jsw </label>
<script type="text/javascript">
    let value = {% echo("10") %};  // let value = 10;
    let value = "{% echo("10") %}";  // let value = "10";
</script>
```
3. 可以将Java的变量在模板中使用
```shell
import java.util.HashMap;
import java.util.Map;

// TODO类似下方的渲染方式，待功能实现
JsTemplate temp = new JsTemplate();
temp.set("j_value", "你好");
temp.set("v", new ArrayList<Object>());
// 直接渲染模板代码
temp.render("<h2>{% echo(j_value) %}</h2>");  // 输出<h2>你好</h2>
temp.render("{% for(let k in v){ echo(k); } %}");  // 迭代输出列表
// 渲染模板文件
temp.render("index.html"); 
```
4. 变量声明
```html
<!-- index.html -->
<div>
    {%
        let value = 10;  // 变量必须使用let进行声明，有效作用范围在当前文件
        echo("<h2>数量：", value, "</h2>");  // 常规的拼接
        echo(`<h2>数量:${value}</h2>`);   // 采用插入替换型
    %}
</div>
<div>
    {%
      echo("<label>", value, "</label>");  // 这里也可以访问
    %}
</div>
```
5. 分支语句
```html
<!-- index.html -->
<div>
    {%
        if(user.value == 3)   // user来自java的对象
        {
            echo("<p>value: 3</p>");
        }elif(user.value == 4)  // 支持elif多分支
        {
        }else   // if if-else  if-elif-else
        {
        }
    %}
</div>
```
6. while循环语句【可能废弃，不做while】
```html
<!-- index.html -->
<div>
    {%
        echo("<ul>");
        let i = 10;
        while(--i > 0)  // while条件判断
        {
            echo("<li>", i, "</li>"); // 内部可以使用分支语句，可以break和continue
        }
        echo("</ul>");
    %}
</div>
```
7. for循环语句
```html
<!-- index.html -->
<div>
    {%
        for(let i = 0; i < 10; i++)  // 这里的i只有在当前for内部有效
        {
            echo("xxx");  // 内部可以使用其他语句嵌套，支持break和continue
        }
    %}
</div>
```
8. for迭代器
```html
<!-- index.html -->
<div>
    {%
        for(let k in list)  // let list=[1,3,4]  支持本地列表和java导出的list
        {
            // 迭代出来就是list里面的元素  【思考】改成js的那种迭代出来是下标？然后list[k]?
        }
        for(let k in map)  // let map={"a":3, "b":2}  支持本地字典和java导出的map
        {
            // 迭代出来的是key，需要使用map[key] 获取内容
        }
    %}
</div>
```
9. 函数
```html
<!-- index.html -->
<div>
    {%
        function add(a, b)  // 函数的常规声明
        {
            return a + b;
        }
        
        let sub = function(a, b)  // 采用赋值的方式声明
        {
            return a - b ;
        }
        echo("value:", add(1, 3));
        // 函数也可以放到list和map里面作为元素，也可以是函数call里面作为参数。
        let funcs = [function(){}, function(){}]
        let funcs = {"add": function(a,b){return a + b;}}
        call(1, function(b, a){}) // call可以是脚本的函数也可以是java导出的函数
    %}
</div>
```