# Clarity

Clarity is a simple Android custom view for displaying character-colored text.
This is common is password manager applications where numbers, letters, and symbols are shown together,
but given different colors in order to make them easier to distinguish.

### Usage

#### XML

```xml
<com.trevorhalvorson.clarity.ClarityView
        android:id="@+id/clarity_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:clarity_lowerAlphabetColor="@color/colorLowerAlpha"
        app:clarity_numberColor="@color/colorNumber"
        app:clarity_symbolColor="@color/colorSymbol"
        app:clarity_upperAlphabetColor="@color/colorUpperAlpha"
        tools:text="H3lL0 WOR1d" />
```

#### Java

```java
ClarityView clarityView = (ClarityView) findViewById(R.id.clarity_view);
clarityView.setNumberColor(ContextCompat.getColor(this, R.color.colorNumber));
clarityView.setSymbolColor(ContextCompat.getColor(this, R.color.colorSymbol));
clarityView.setUpperAlphabetColor(ContextCompat.getColor(this, R.color.colorUpperAlpha));
clarityView.setLowerAlphabetColor(ContextCompat.getColor(this, R.color.colorLowerAlpha));
clarityView.setText("H3lL0 WOR1d");
```

Also see the `clarity-sample` module
