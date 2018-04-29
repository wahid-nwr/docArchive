/*      */ package leadtools.internal;
/*      */ 
/*      */ public class KnownColors
/*      */ {
/*      */   public static String matchColor(String colorString, MatchColorResult res)
/*      */   {
/*   21 */     String str = colorString.trim();
/*   22 */     int length = str.length();
/*      */ 
/*   24 */     if (((length == 4) || (length == 5) || (length == 7) || (length == 9)) && (str.charAt(0) == '#')) {
/*   25 */       res.isNumericColor = true;
/*   26 */       res.isKnownColor = false;
/*   27 */       res.isRgb = false;
/*   28 */       res.isRgba = false;
/*   29 */       return str;
/*      */     }
/*   31 */     res.isNumericColor = false;
/*   32 */     if (str.startsWith("sc#"))
/*      */     {
/*   34 */       res.isNumericColor = false;
/*   35 */       res.isKnownColor = false;
/*   36 */       res.isRgb = false;
/*   37 */       res.isRgba = false;
/*      */     }
/*   39 */     if ((length >= 5) && (str.startsWith("rgba")))
/*      */     {
/*   41 */       res.isNumericColor = false;
/*   42 */       res.isKnownColor = false;
/*   43 */       res.isRgb = false;
/*   44 */       res.isRgba = true;
/*   45 */       return str;
/*      */     }
/*   47 */     if ((length >= 4) && (str.startsWith("rgb")))
/*      */     {
/*   49 */       res.isNumericColor = false;
/*   50 */       res.isKnownColor = false;
/*   51 */       res.isRgb = true;
/*   52 */       res.isRgba = false;
/*   53 */       return str;
/*      */     }
/*      */ 
/*   56 */     res.isKnownColor = true;
/*   57 */     return str;
/*      */   }
/*      */ 
/*      */   public static long colorStringToKnownColor(String colorString) {
/*   61 */     if (colorString != null) {
/*   62 */       String str = colorString.toUpperCase();
/*   63 */       switch (str.length())
/*      */       {
/*      */       case 3:
/*   66 */         if (!str.equals("RED"))
/*      */         {
/*   68 */           if (str.equals("TAN"))
/*      */           {
/*   72 */             return -2968436L;
/*      */           }
/*      */         } else return -65536L;
/*      */         break;
/*      */       case 4:
/*   77 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'A':
/*   80 */           if (str.equals("AQUA"))
/*      */           {
/*   84 */             return -16711681L;
/*      */           }break;
/*      */         case 'B':
/*   87 */           if (str.equals("BLUE"))
/*      */           {
/*   91 */             return -16776961L;
/*      */           }break;
/*      */         case 'C':
/*   94 */           if (str.equals("CYAN"))
/*      */           {
/*   98 */             return -16711681L;
/*      */           }break;
/*      */         case 'G':
/*  101 */           if (str.equals("GOLD"))
/*      */           {
/*  103 */             return -10496L;
/*      */           }
/*  105 */           if (str.equals("GRAY"))
/*      */           {
/*  109 */             return -8355712L;
/*      */           }break;
/*      */         case 'L':
/*  112 */           if (str.equals("LIME"))
/*      */           {
/*  116 */             return -16711936L;
/*      */           }break;
/*      */         case 'M':
/*  119 */           if (str.equals("MENU"))
/*      */           {
/*  123 */             return -986896L;
/*      */           }break;
/*      */         case 'N':
/*  126 */           if (str.equals("NAVY"))
/*      */           {
/*  130 */             return -16777088L;
/*      */           }break;
/*      */         case 'P':
/*  133 */           if (str.equals("PERU"))
/*      */           {
/*  135 */             return -3308225L;
/*      */           }
/*  137 */           if (str.equals("PINK"))
/*      */           {
/*  139 */             return -16181L;
/*      */           }
/*  141 */           if (str.equals("PLUM"))
/*      */           {
/*  145 */             return -2252579L;
/*      */           }break;
/*      */         case 'S':
/*  148 */           if (str.equals("SNOW"))
/*      */           {
/*  152 */             return -1286L;
/*      */           }break;
/*      */         case 'T':
/*  155 */           if (str.equals("TEAL"))
/*      */           {
/*  159 */             return -16744320L; } break;
/*      */         case 'D':
/*      */         case 'E':
/*      */         case 'F':
/*      */         case 'H':
/*      */         case 'I':
/*      */         case 'J':
/*      */         case 'K':
/*      */         case 'O':
/*      */         case 'Q':
/*  161 */         case 'R': } break;
/*      */       case 5:
/*  164 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'A':
/*  167 */           if (str.equals("AZURE"))
/*      */           {
/*  171 */             return -983041L;
/*      */           }break;
/*      */         case 'B':
/*  174 */           if (str.equals("BEIGE"))
/*      */           {
/*  176 */             return -657956L;
/*      */           }
/*  178 */           if (str.equals("BLACK"))
/*      */           {
/*  180 */             return -16777216L;
/*      */           }
/*  182 */           if (str.equals("BROWN"))
/*      */           {
/*  186 */             return -5952982L;
/*      */           }break;
/*      */         case 'C':
/*  189 */           if (str.equals("CORAL"))
/*      */           {
/*  193 */             return -32944L;
/*      */           }break;
/*      */         case 'G':
/*  196 */           if (str.equals("GREEN"))
/*      */           {
/*  200 */             return -16744448L;
/*      */           }break;
/*      */         case 'I':
/*  203 */           if (str.equals("IVORY"))
/*      */           {
/*  207 */             return -16L;
/*      */           }break;
/*      */         case 'K':
/*  210 */           if (str.equals("KHAKI"))
/*      */           {
/*  214 */             return -989556L;
/*      */           }break;
/*      */         case 'L':
/*  217 */           if (str.equals("LINEN"))
/*      */           {
/*  221 */             return -331546L;
/*      */           }break;
/*      */         case 'O':
/*  224 */           if (str.equals("OLIVE"))
/*      */           {
/*  228 */             return -8355840L;
/*      */           }break;
/*      */         case 'W':
/*  231 */           if (str.equals("WHEAT"))
/*      */           {
/*  233 */             return -663885L;
/*      */           }
/*  235 */           if (str.equals("WHITE"))
/*      */           {
/*  239 */             return -1L; } break;
/*      */         case 'D':
/*      */         case 'E':
/*      */         case 'F':
/*      */         case 'H':
/*      */         case 'J':
/*      */         case 'M':
/*      */         case 'N':
/*      */         case 'P':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'S':
/*      */         case 'T':
/*      */         case 'U':
/*  241 */         case 'V': } break;
/*      */       case 6:
/*  244 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'M':
/*  247 */           if (str.equals("MAROON"))
/*      */           {
/*  251 */             return -8388608L;
/*      */           }break;
/*      */         case 'O':
/*  254 */           if (str.equals("ORANGE"))
/*      */           {
/*  256 */             return -23296L;
/*      */           }
/*  258 */           if (str.equals("ORCHID"))
/*      */           {
/*  262 */             return -2461482L;
/*      */           }break;
/*      */         case 'P':
/*  265 */           if (str.equals("PURPLE"))
/*      */           {
/*  269 */             return -8388480L;
/*      */           }break;
/*      */         case 'S':
/*  272 */           if (str.equals("SALMON"))
/*      */           {
/*  274 */             return -360334L;
/*      */           }
/*  276 */           if (str.equals("SIENNA"))
/*      */           {
/*  278 */             return -6270419L;
/*      */           }
/*  280 */           if (str.equals("SILVER"))
/*      */           {
/*  284 */             return -4144960L;
/*      */           }break;
/*      */         case 'T':
/*  287 */           if (str.equals("TOMATO"))
/*      */           {
/*  291 */             return -40121L;
/*      */           }break;
/*      */         case 'V':
/*  294 */           if (str.equals("VIOLET"))
/*      */           {
/*  298 */             return -1146130L;
/*      */           }break;
/*      */         case 'Y':
/*  301 */           if (str.equals("YELLOW"))
/*      */           {
/*  305 */             return -256L;
/*      */           }break;
/*      */         case 'I':
/*  308 */           if (str.equals("INDIGO"))
/*      */           {
/*  312 */             return -11861886L;
/*      */           }break;
/*      */         case 'B':
/*  315 */           if (str.equals("BISQUE"))
/*      */           {
/*  319 */             return -6972L;
/*      */           }break;
/*      */         case 'W':
/*  322 */           if (str.equals("WINDOW"))
/*      */           {
/*  326 */             return -986896L; } break;
/*      */         case 'C':
/*      */         case 'D':
/*      */         case 'E':
/*      */         case 'F':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'J':
/*      */         case 'K':
/*      */         case 'L':
/*      */         case 'N':
/*      */         case 'Q':
/*      */         case 'R':
/*      */         case 'U':
/*  328 */         case 'X': } break;
/*      */       case 7:
/*  331 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'C':
/*  334 */           if (str.equals("CRIMSON"))
/*      */           {
/*  338 */             return -2354116L;
/*      */           }break;
/*      */         case 'D':
/*  341 */           if (str.equals("DARKRED"))
/*      */           {
/*  343 */             return -7667712L;
/*      */           }
/*  345 */           if (str.equals("DIMGRAY"))
/*      */           {
/*  349 */             return -9868951L;
/*      */           }break;
/*      */         case 'F':
/*  352 */           if (str.equals("FUCHSIA"))
/*      */           {
/*  356 */             return -65281L;
/*      */           }break;
/*      */         case 'H':
/*  359 */           if (str.equals("HOTPINK"))
/*      */           {
/*  363 */             return -38476L;
/*      */           }break;
/*      */         case 'M':
/*  366 */           if (str.equals("MAGENTA"))
/*      */           {
/*  370 */             return -65281L;
/*      */           }break;
/*      */         case 'O':
/*  373 */           if (str.equals("OLDLACE"))
/*      */           {
/*  377 */             return -133658L;
/*      */           }break;
/*      */         case 'S':
/*  380 */           if (str.equals("SKYBLUE"))
/*      */           {
/*  384 */             return -7876885L;
/*      */           }break;
/*      */         case 'T':
/*  387 */           if (str.equals("THISTLE"))
/*      */           {
/*  391 */             return -2572328L; } break;
/*      */         case 'E':
/*      */         case 'G':
/*      */         case 'I':
/*      */         case 'J':
/*      */         case 'K':
/*      */         case 'L':
/*      */         case 'N':
/*      */         case 'P':
/*      */         case 'Q':
/*  393 */         case 'R': } break;
/*      */       case 8:
/*  397 */         char ch3 = str.charAt(0);
/*  398 */         if (ch3 > 'H')
/*      */         {
/*  400 */           switch (ch3)
/*      */           {
/*      */           case 'L':
/*  403 */             if (str.equals("LAVENDER"))
/*      */             {
/*  407 */               return -1644806L;
/*      */             }break;
/*      */           case 'I':
/*  410 */             if (str.equals("INFOTEXT"))
/*      */             {
/*  414 */               return -16777216L;
/*      */             }break;
/*      */           case 'M':
/*  417 */             if (str.equals("MENUTEXT"))
/*      */             {
/*  419 */               return -16777216L;
/*      */             }
/*  421 */             if (str.equals("MOCCASIN"))
/*      */             {
/*  425 */               return -6987L;
/*      */             }break;
/*      */           case 'S':
/*  428 */             if (str.equals("SEAGREEN"))
/*      */             {
/*  430 */               return -13726889L;
/*      */             }
/*  432 */             if (str.equals("SEASHELL"))
/*      */             {
/*  436 */               return -2578L;
/*      */             }break;
/*      */           }
/*      */         }
/*  440 */         else switch (ch3)
/*      */           {
/*      */           case 'C':
/*  443 */             if (str.equals("CORNSILK"))
/*      */             {
/*  447 */               return -1828L;
/*      */             }break;
/*      */           case 'D':
/*  450 */             if (str.equals("DARKBLUE"))
/*      */             {
/*  452 */               return -16777077L;
/*      */             }
/*  454 */             if (str.equals("DARKCYAN"))
/*      */             {
/*  456 */               return -16741493L;
/*      */             }
/*  458 */             if (str.equals("DARKGRAY"))
/*      */             {
/*  460 */               return -5658199L;
/*      */             }
/*  462 */             if (str.equals("DEEPPINK"))
/*      */             {
/*  466 */               return -60269L;
/*      */             }break;
/*      */           case 'G':
/*  469 */             if (str.equals("GRAYTEXT"))
/*      */             {
/*  473 */               return -9605779L;
/*      */             }break;
/*      */           case 'H':
/*  476 */             if (str.equals("HONEYDEW"))
/*      */             {
/*  480 */               return -983056L; } break;
/*      */           case 'E':
/*  482 */           case 'F': }  break;
/*      */       case 9:
/*  485 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'A':
/*  488 */           if (str.equals("ALICEBLUE"))
/*      */           {
/*  492 */             return -984833L;
/*      */           }break;
/*      */         case 'B':
/*  495 */           if (str.equals("BURLYWOOD"))
/*      */           {
/*  499 */             return -2180985L;
/*      */           }break;
/*      */         case 'C':
/*  502 */           if (str.equals("CADETBLUE"))
/*      */           {
/*  504 */             return -10510688L;
/*      */           }
/*  506 */           if (str.equals("CHOCOLATE"))
/*      */           {
/*  510 */             return -2987746L;
/*      */           }break;
/*      */         case 'D':
/*  513 */           if (str.equals("DARKGREEN"))
/*      */           {
/*  515 */             return -16751616L;
/*      */           }
/*  517 */           if (str.equals("DARKKHAKI"))
/*      */           {
/*  521 */             return -4343957L;
/*      */           }break;
/*      */         case 'F':
/*  524 */           if (str.equals("FIREBRICK"))
/*      */           {
/*  528 */             return -5103070L;
/*      */           }break;
/*      */         case 'G':
/*  531 */           if (str.equals("GAINSBORO"))
/*      */           {
/*  533 */             return -2302756L;
/*      */           }
/*  535 */           if (str.equals("GOLDENROD"))
/*      */           {
/*  539 */             return -2448096L;
/*      */           }break;
/*      */         case 'H':
/*  542 */           if (str.equals("HIGHLIGHT"))
/*      */           {
/*  546 */             return -13395457L;
/*      */           }break;
/*      */         case 'I':
/*  549 */           if (str.equals("INDIANRED"))
/*      */           {
/*  553 */             return -3318692L;
/*      */           }break;
/*      */         case 'L':
/*  556 */           if (str.equals("LAWNGREEN"))
/*      */           {
/*  558 */             return -8586240L;
/*      */           }
/*  560 */           if (str.equals("LIGHTBLUE"))
/*      */           {
/*  562 */             return -5383962L;
/*      */           }
/*  564 */           if (str.equals("LIGHTCYAN"))
/*      */           {
/*  566 */             return -2031617L;
/*      */           }
/*  568 */           if (str.equals("LIGHTGRAY"))
/*      */           {
/*  570 */             return -2894893L;
/*      */           }
/*  572 */           if (str.equals("LIGHTPINK"))
/*      */           {
/*  574 */             return -18751L;
/*      */           }
/*  576 */           if (str.equals("LIMEGREEN"))
/*      */           {
/*  580 */             return -13447886L;
/*      */           }break;
/*      */         case 'M':
/*  583 */           if (str.equals("MINTCREAM"))
/*      */           {
/*  585 */             return -655366L;
/*      */           }
/*  587 */           if (str.equals("MISTYROSE"))
/*      */           {
/*  591 */             return -6943L;
/*      */           }break;
/*      */         case 'O':
/*  594 */           if (str.equals("OLIVEDRAB"))
/*      */           {
/*  596 */             return -9728477L;
/*      */           }
/*  598 */           if (str.equals("ORANGERED"))
/*      */           {
/*  602 */             return -47872L;
/*      */           }break;
/*      */         case 'P':
/*  605 */           if (str.equals("PALEGREEN"))
/*      */           {
/*  607 */             return -6751336L;
/*      */           }
/*  609 */           if (str.equals("PEACHPUFF"))
/*      */           {
/*  613 */             return -9543L;
/*      */           }break;
/*      */         case 'R':
/*  616 */           if (str.equals("ROSYBROWN"))
/*      */           {
/*  618 */             return -4419697L;
/*      */           }
/*  620 */           if (str.equals("ROYALBLUE"))
/*      */           {
/*  624 */             return -12490271L;
/*      */           }break;
/*      */         case 'S':
/*  627 */           if (str.equals("SCROLLBAR"))
/*      */           {
/*  629 */             return -3618616L;
/*      */           }
/*  631 */           if (str.equals("SLATEBLUE"))
/*      */           {
/*  633 */             return -9807155L;
/*      */           }
/*  635 */           if (str.equals("SLATEGRAY"))
/*      */           {
/*  637 */             return -9404272L;
/*      */           }
/*  639 */           if (str.equals("STEELBLUE"))
/*      */           {
/*  643 */             return -12156236L;
/*      */           }break;
/*      */         case 'T':
/*  646 */           if (str.equals("TURQUOISE"))
/*      */           {
/*  650 */             return -12525360L; } break;
/*      */         case 'E':
/*      */         case 'J':
/*      */         case 'K':
/*      */         case 'N':
/*  652 */         case 'Q': } break;
/*      */       case 10:
/*  656 */         char ch2 = str.charAt(0);
/*  657 */         if (ch2 > 'P')
/*      */         {
/*  659 */           switch (ch2)
/*      */           {
/*      */           case 'S':
/*  662 */             if (str.equals("SANDYBROWN"))
/*      */             {
/*  666 */               return -744352L;
/*      */             }break;
/*      */           case 'T':
/*  669 */             if (str.equals("THREEDFACE"))
/*      */             {
/*  673 */               return -986896L;
/*      */             }break;
/*      */           case 'W':
/*  676 */             if (str.equals("WINDOWTEXT"))
/*      */             {
/*  678 */               return -16777216L;
/*      */             }
/*  680 */             if (str.equals("WHITESMOKE"))
/*      */             {
/*  684 */               return -657931L; } break;
/*      */           case 'U':
/*      */           case 'V':
/*      */           }
/*      */         } else switch (ch2)
/*      */           {
/*      */           case 'A':
/*  691 */             if (str.equals("AQUAMARINE"))
/*      */             {
/*  695 */               return -8388652L;
/*      */             }break;
/*      */           case 'B':
/*  698 */             if (str.equals("BACKGROUND"))
/*      */             {
/*  700 */               return -16777216L;
/*      */             }
/*  702 */             if (str.equals("BUTTONFACE"))
/*      */             {
/*  704 */               return -986896L;
/*      */             }
/*  706 */             if (str.equals("BUTTONTEXT"))
/*      */             {
/*  708 */               return -16777216L;
/*      */             }
/*  710 */             if (str.equals("BLUEVIOLET"))
/*      */             {
/*  714 */               return -7722014L;
/*      */             }break;
/*      */           case 'C':
/*  717 */             if (str.equals("CHARTREUSE"))
/*      */             {
/*  721 */               return -8388864L;
/*      */             }break;
/*      */           case 'D':
/*  724 */             if (str.equals("DARKORANGE"))
/*      */             {
/*  726 */               return -29696L;
/*      */             }
/*  728 */             if (str.equals("DARKORCHID"))
/*      */             {
/*  730 */               return -6737204L;
/*      */             }
/*  732 */             if (str.equals("DARKSALMON"))
/*      */             {
/*  734 */               return -1468806L;
/*      */             }
/*  736 */             if (str.equals("DARKVIOLET"))
/*      */             {
/*  738 */               return -7077677L;
/*      */             }
/*  740 */             if (str.equals("DODGERBLUE"))
/*      */             {
/*  744 */               return -14774017L;
/*      */             }break;
/*      */           case 'G':
/*  747 */             if (str.equals("GHOSTWHITE"))
/*      */             {
/*  751 */               return -460545L;
/*      */             }break;
/*      */           case 'L':
/*  754 */             if (str.equals("LIGHTCORAL"))
/*      */             {
/*  756 */               return -1015680L;
/*      */             }
/*  758 */             if (str.equals("LIGHTGREEN"))
/*      */             {
/*  762 */               return -7278960L;
/*      */             }break;
/*      */           case 'M':
/*  765 */             if (str.equals("MEDIUMBLUE"))
/*      */             {
/*  769 */               return -16777011L;
/*      */             }break;
/*      */           case 'P':
/*  772 */             if (str.equals("PAPAYAWHIP"))
/*      */             {
/*  774 */               return -4139L;
/*      */             }
/*  776 */             if (str.equals("POWDERBLUE"))
/*      */             {
/*  780 */               return -5185306L; } break;
/*      */           case 'E':
/*      */           case 'F':
/*      */           case 'H':
/*      */           case 'I':
/*      */           case 'J':
/*      */           case 'K':
/*      */           case 'N':
/*  782 */           case 'O': }  break;
/*      */       case 11:
/*  786 */         char ch = str.charAt(0);
/*  787 */         if (ch > 'N')
/*      */         {
/*  789 */           switch (ch)
/*      */           {
/*      */           case 'S':
/*  792 */             if (!str.equals("SADDLEBROWN"))
/*      */             {
/*  794 */               if (str.equals("SPRINGGREEN"))
/*      */               {
/*  798 */                 return -16711809L;
/*      */               }
/*      */             } else return -7650029L;
/*      */             break;
/*      */           case 'T':
/*  803 */             if (str.equals("TRANSPARENT"))
/*      */             {
/*  807 */               return 16777215L;
/*      */             }break;
/*      */           case 'W':
/*  810 */             if (str.equals("WINDOWFRAME"))
/*      */             {
/*  814 */               return -10197916L;
/*      */             }break;
/*      */           case 'Y':
/*  817 */             if (str.equals("YELLOWGREEN"))
/*      */             {
/*  821 */               return -6632142L; } break;
/*      */           case 'U':
/*      */           case 'V':
/*      */           case 'X': } 
/*      */         } else switch (ch)
/*      */           {
/*      */           case 'C':
/*  828 */             if (str.equals("CAPTIONTEXT"))
/*      */             {
/*  832 */               return -16777216L;
/*      */             }break;
/*      */           case 'D':
/*  835 */             if (str.equals("DARKMAGENTA"))
/*      */             {
/*  837 */               return -7667573L;
/*      */             }
/*  839 */             if (str.equals("DEEPSKYBLUE"))
/*      */             {
/*  843 */               return -16728065L;
/*      */             }break;
/*      */           case 'F':
/*  846 */             if (str.equals("FLORALWHITE"))
/*      */             {
/*  848 */               return -1296L;
/*      */             }
/*  850 */             if (str.equals("FORESTGREEN"))
/*      */             {
/*  854 */               return -14513374L;
/*      */             }break;
/*      */           case 'G':
/*  857 */             if (str.equals("GREENYELLOW"))
/*      */             {
/*  861 */               return -5374161L;
/*      */             }break;
/*      */           case 'L':
/*  864 */             if (str.equals("LIGHTSALMON"))
/*      */             {
/*  866 */               return -24454L;
/*      */             }
/*  868 */             if (str.equals("LIGHTYELLOW"))
/*      */             {
/*  872 */               return -32L;
/*      */             }break;
/*      */           case 'N':
/*  875 */             if (str.equals("NAVAJOWHITE"))
/*      */             {
/*  879 */               return -8531L; } break;
/*      */           case 'E':
/*      */           case 'H':
/*      */           case 'I':
/*      */           case 'J':
/*      */           case 'K':
/*  881 */           case 'M': }  break;
/*      */       case 12:
/*  884 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'L':
/*  887 */           if (str.equals("LIGHTSKYBLUE"))
/*      */           {
/*  889 */             return -7876870L;
/*      */           }
/*  891 */           if (str.equals("LEMONCHIFFON"))
/*      */           {
/*  895 */             return -1331L;
/*      */           }break;
/*      */         case 'M':
/*  898 */           if (str.equals("MEDIUMORCHID"))
/*      */           {
/*  900 */             return -4565549L;
/*      */           }
/*  902 */           if (str.equals("MEDIUMPURPLE"))
/*      */           {
/*  904 */             return -7114533L;
/*      */           }
/*  906 */           if (str.equals("MIDNIGHTBLUE"))
/*      */           {
/*  910 */             return -15132304L;
/*      */           }break;
/*      */         case 'D':
/*  913 */           if (str.equals("DARKSEAGREEN"))
/*      */           {
/*  917 */             return -7357297L;
/*      */           }break;
/*      */         case 'A':
/*  920 */           if (str.equals("ACTIVEBORDER"))
/*      */           {
/*  922 */             return -4934476L;
/*      */           }
/*  924 */           if (str.equals("APPWORKSPACE"))
/*      */           {
/*  926 */             return -5526613L;
/*      */           }
/*  928 */           if (str.equals("ANTIQUEWHITE"))
/*      */           {
/*  932 */             return -332841L;
/*      */           }break;
/*      */         case 'B':
/*  935 */           if (str.equals("BUTTONSHADOW"))
/*      */           {
/*  939 */             return -6250336L; } break;
/*      */         case 'C':
/*      */         case 'E':
/*      */         case 'F':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'I':
/*      */         case 'J':
/*  941 */         case 'K': } break;
/*      */       case 13:
/*  944 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'A':
/*  947 */           if (str.equals("ACTIVECAPTION"))
/*      */           {
/*  951 */             return -6703919L;
/*      */           }break;
/*      */         case 'D':
/*  954 */           if (str.equals("DARKSLATEBLUE"))
/*      */           {
/*  956 */             return -12042869L;
/*      */           }
/*  958 */           if (str.equals("DARKSLATEGRAY"))
/*      */           {
/*  960 */             return -13676721L;
/*      */           }
/*  962 */           if (str.equals("DARKGOLDENROD"))
/*      */           {
/*  964 */             return -4684277L;
/*      */           }
/*  966 */           if (str.equals("DARKTURQUOISE"))
/*      */           {
/*  970 */             return -16724271L;
/*      */           }break;
/*      */         case 'H':
/*  973 */           if (str.equals("HIGHLIGHTTEXT"))
/*      */           {
/*  977 */             return -1L;
/*      */           }break;
/*      */         case 'L':
/*  980 */           if (str.equals("LIGHTSEAGREEN"))
/*      */           {
/*  982 */             return -14634326L;
/*      */           }
/*  984 */           if (str.equals("LAVENDERBLUSH"))
/*      */           {
/*  988 */             return -3851L;
/*      */           }break;
/*      */         case 'P':
/*  991 */           if (str.equals("PALEGOLDENROD"))
/*      */           {
/*  993 */             return -1120086L;
/*      */           }
/*  995 */           if (str.equals("PALETURQUOISE"))
/*      */           {
/*  997 */             return -5247250L;
/*      */           }
/*  999 */           if (str.equals("PALEVIOLETRED"))
/*      */           {
/* 1003 */             return -2396013L;
/*      */           }break;
/* 1005 */         }break;
/*      */       case 14:
/* 1008 */         switch (str.charAt(0))
/*      */         {
/*      */         case 'B':
/* 1011 */           if (str.equals("BLANCHEDALMOND"))
/*      */           {
/* 1015 */             return -5171L;
/*      */           }break;
/*      */         case 'C':
/* 1018 */           if (str.equals("CORNFLOWERBLUE"))
/*      */           {
/* 1022 */             return -10185235L;
/*      */           }break;
/*      */         case 'D':
/* 1025 */           if (str.equals("DARKOLIVEGREEN"))
/*      */           {
/* 1029 */             return -11179217L;
/*      */           }break;
/*      */         case 'I':
/* 1032 */           if (str.equals("INACTIVEBORDER"))
/*      */           {
/* 1034 */             return -722948L;
/*      */           }
/* 1036 */           if (str.equals("INFOBACKGROUND"))
/*      */           {
/* 1040 */             return -31L;
/*      */           }break;
/*      */         case 'L':
/* 1043 */           if (str.equals("LIGHTSLATEGRAY"))
/*      */           {
/* 1045 */             return -8943463L;
/*      */           }
/* 1047 */           if (str.equals("LIGHTSTEELBLUE"))
/*      */           {
/* 1051 */             return -5192482L;
/*      */           }break;
/*      */         case 'M':
/* 1054 */           if (str.equals("MEDIUMSEAGREEN"))
/*      */           {
/* 1058 */             return -12799119L; } break;
/*      */         case 'E':
/*      */         case 'F':
/*      */         case 'G':
/*      */         case 'H':
/*      */         case 'J':
/* 1060 */         case 'K': } break;
/*      */       case 15:
/* 1063 */         if (!str.equals("MEDIUMSLATEBLUE"))
/*      */         {
/* 1065 */           if (str.equals("MEDIUMTURQUOISE"))
/*      */           {
/* 1067 */             return -12004916L;
/*      */           }
/* 1069 */           if (str.equals("MEDIUMVIOLETRED"))
/*      */           {
/* 1071 */             return -3730043L;
/*      */           }
/* 1073 */           if (str.equals("BUTTONHIGHLIGHT"))
/*      */           {
/* 1075 */             return -1L;
/*      */           }
/* 1077 */           if (str.equals("INACTIVECAPTION"))
/*      */           {
/* 1079 */             return -4207141L;
/*      */           }
/* 1081 */           if (str.equals("THREEDHIGHLIGHT"))
/*      */           {
/* 1083 */             return -1842205L;
/*      */           }
/*      */         }
/*      */         else {
/* 1087 */           return -8689426L;
/*      */         }break;
/*      */       case 16:
/* 1090 */         if (str.equals("THREEDDARKSHADOW"))
/*      */         {
/* 1092 */           return -9868951L;
/*      */         }
/* 1094 */         if (str.equals("MEDIUMAQUAMARINE"))
/*      */         {
/* 1098 */           return -10039894L;
/*      */         }break;
/*      */       case 17:
/* 1101 */         if (str.equals("THREEDLIGHTSHADOW"))
/*      */         {
/* 1103 */           return -1L;
/*      */         }
/* 1105 */         if (str.equals("MEDIUMSPRINGGREEN"))
/*      */         {
/* 1109 */           return -16713062L;
/*      */         }break;
/*      */       case 19:
/* 1112 */         if (str.equals("INACTIVECAPTIONTEXT"))
/*      */         {
/* 1116 */           return -12366252L;
/*      */         }break;
/*      */       case 20:
/* 1119 */         if (str.equals("LIGHTGOLDENRODYELLOW"))
/*      */         {
/* 1123 */           return -329006L; } break;
/*      */       case 18:
/*      */       }
/* 1126 */     }return 1L;
/*      */   }
/*      */ 
/*      */   public static class MatchColorResult
/*      */   {
/*      */     public boolean isKnownColor;
/*      */     public boolean isNumericColor;
/*      */     public boolean isRgb;
/*      */     public boolean isRgba;
/*      */   }
/*      */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     leadtools.internal.KnownColors
 * JD-Core Version:    0.6.2
 */