# CodeInputView 自定义code输入框

[![Download](https://api.bintray.com/packages/bintray/jcenter/code-input-view/images/download.svg) ](https://bintray.com/bintray/jcenter/code-input-view/_latestVersion) 
[![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=11)

## Futures特性

1. 支持纯数字/字母/自动大写/密码/纯数字密码 输入

2. 支持使用自定义键盘

<div>
  <img src="https://github.com/GalenLeo/CodeInputView/blob/master/gif/demo.gif" alt="Demo效果图" height="700dp">
</div>

## Usage如何使用

### Step 1

**Gradle**

    implementation 'com.galenleo.widgets:code-input-view:1.0.0'

### Step 2

    <com.galenleo.widgets.CodeInputView
        android:id="@+id/verification_code_input_view"
        android:layout_width="wrap_content"
        android:layout_height="40dp"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="24dp"
        app:ciItemWidth="40dp"
        app:ciTextSize="@dimen/txt_20"
        app:ciTextColor="@color/title_color"
        app:ciInputType="textCapCharacters"
        app:ciGapWidth="10dp"
        app:ciItemCount="6"
        app:ciItemBackground="@drawable/selector_code_input_item"/>

## Attributes属性

attr|default|description
---|:--|:--
ciItemCount|4|code item count
ciItemWidth||code item width
ciGapWidth|10px|gap between item
ciTextSize|24px|code text size
ciTextColor|Color.CYAN|code text color
ciInputType|text|number（数字）<br>text（字母/数字）<br>textCapCharacters(大写字母/数字)<br>password（密码）<br>numberPassword（数字密码）
ciItemBackground||code item background
ciSoftInputEnable|true|enable to show the soft keyboard<br>是否显示软键盘，使用自定义键盘的时候关闭
