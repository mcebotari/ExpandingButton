package com.mcebotari.expandingbutton

import android.content.Context
import android.widget.Toast

class ExpandingButton {

    companion object{
        fun testMessage(context : Context, message : String){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}