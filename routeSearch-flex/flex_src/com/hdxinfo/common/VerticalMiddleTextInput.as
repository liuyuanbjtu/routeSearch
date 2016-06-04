package com.hdxinfo.common
{
 import mx.controls.Text;
     import mx.controls.TextInput;
     
     public class VerticalMiddleTextInput extends TextInput
     {
         public function VerticalMiddleTextInput()
         {
             super()
         }
         
         override protected function createChildren():void
         {
             super.createChildren();
         }
         
         override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
         {
             super.updateDisplayList( unscaledWidth, unscaledHeight );
             
             this.textField.height = this.textField.textHeight ;
             
             this.textField.y = this.height/2 -this.textField.height/2
         }
     }
}