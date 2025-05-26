### 模板语言

#### 语法
`{{ 表达式 }}` 用于将`表达式`的结果输出(会对HTML内容进行转义)<br>
`{% set x = 表达式 %}` 用于对`x`进行赋值<br>
`{% raw 表达式 %}` 用于将`表达式`的结果使用原始输出(不转义HTML)<br>
`{% if 表达式 %} body {% end %}` 分支语句，只有`表达式`的结果为true才会执行body的内容。<br>
完整版分支控制
```
{% if 表达式 %}
    body
{% elif 表达式 %} 
    body
{% else %}
    body
{% end %}
```
while循环
```
{% while 表达式 %} 
{% end %}
```
迭代循环
```
{% for k in iter %}
{% end %}
```