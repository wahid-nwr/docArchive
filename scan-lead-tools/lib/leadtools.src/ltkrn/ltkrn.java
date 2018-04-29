/*     */ package ltkrn;
/*     */ 
/*     */ public class ltkrn
/*     */ {
/*     */   public static void L_Point_Make(L_POINT point, int x, int y)
/*     */   {
/*  13 */     ltkrnJNI.L_Point_Make(L_POINT.getCPtr(point), point, x, y);
/*     */   }
/*     */ 
/*     */   public static void L_Point_Empty(L_POINT point) {
/*  17 */     ltkrnJNI.L_Point_Empty(L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static int L_Point_IsEmpty(L_POINT point) {
/*  21 */     return ltkrnJNI.L_Point_IsEmpty(L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static int L_Point_IsEqual(L_POINT point1, L_POINT point2) {
/*  25 */     return ltkrnJNI.L_Point_IsEqual(L_POINT.getCPtr(point1), point1, L_POINT.getCPtr(point2), point2);
/*     */   }
/*     */ 
/*     */   public static void L_Point_ToPointD(L_POINT point, L_POINTD result) {
/*  29 */     ltkrnJNI.L_Point_ToPointD(L_POINT.getCPtr(point), point, L_POINTD.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_Size_Make(L_SIZE size, int width, int height) {
/*  33 */     ltkrnJNI.L_Size_Make(L_SIZE.getCPtr(size), size, width, height);
/*     */   }
/*     */ 
/*     */   public static void L_Size_Empty(L_SIZE size) {
/*  37 */     ltkrnJNI.L_Size_Empty(L_SIZE.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_Size_IsEmpty(L_SIZE size) {
/*  41 */     return ltkrnJNI.L_Size_IsEmpty(L_SIZE.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_Size_IsEqual(L_SIZE size1, L_SIZE size2) {
/*  45 */     return ltkrnJNI.L_Size_IsEqual(L_SIZE.getCPtr(size1), size1, L_SIZE.getCPtr(size2), size2);
/*     */   }
/*     */ 
/*     */   public static void L_Size_ToSizeD(L_SIZE size, L_SIZED result) {
/*  49 */     ltkrnJNI.L_Size_ToSizeD(L_SIZE.getCPtr(size), size, L_SIZED.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Make(L_RECT rect, int x, int y, int width, int height) {
/*  53 */     ltkrnJNI.L_Rect_Make(L_RECT.getCPtr(rect), rect, x, y, width, height);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_FromLTRB(L_RECT rect, int left, int top, int right, int bottom) {
/*  57 */     ltkrnJNI.L_Rect_FromLTRB(L_RECT.getCPtr(rect), rect, left, top, right, bottom);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Empty(L_RECT rect) {
/*  61 */     ltkrnJNI.L_Rect_Empty(L_RECT.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_IsEmpty(L_RECT rect) {
/*  65 */     return ltkrnJNI.L_Rect_IsEmpty(L_RECT.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_IsEqual(L_RECT rect1, L_RECT rect2) {
/*  69 */     return ltkrnJNI.L_Rect_IsEqual(L_RECT.getCPtr(rect1), rect1, L_RECT.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_Width(L_RECT rect) {
/*  73 */     return ltkrnJNI.L_Rect_Width(L_RECT.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_Height(L_RECT rect) {
/*  77 */     return ltkrnJNI.L_Rect_Height(L_RECT.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_TopLeft(L_RECT rect, L_POINT point) {
/*  81 */     ltkrnJNI.L_Rect_TopLeft(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_TopRight(L_RECT rect, L_POINT point) {
/*  85 */     ltkrnJNI.L_Rect_TopRight(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_BottomRight(L_RECT rect, L_POINT point) {
/*  89 */     ltkrnJNI.L_Rect_BottomRight(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_BottomLeft(L_RECT rect, L_POINT point) {
/*  93 */     ltkrnJNI.L_Rect_BottomLeft(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Location(L_RECT rect, L_POINT point) {
/*  97 */     ltkrnJNI.L_Rect_Location(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Size(L_RECT rect, L_SIZE size) {
/* 101 */     ltkrnJNI.L_Rect_Size(L_RECT.getCPtr(rect), rect, L_SIZE.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_ContainsPoint(L_RECT rect, L_POINT testPoint) {
/* 105 */     return ltkrnJNI.L_Rect_ContainsPoint(L_RECT.getCPtr(rect), rect, L_POINT.getCPtr(testPoint), testPoint);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_ContainsRect(L_RECT rect, L_RECT testRect) {
/* 109 */     return ltkrnJNI.L_Rect_ContainsRect(L_RECT.getCPtr(rect), rect, L_RECT.getCPtr(testRect), testRect);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Inflate(L_RECT rect, L_SIZE size) {
/* 113 */     ltkrnJNI.L_Rect_Inflate(L_RECT.getCPtr(rect), rect, L_SIZE.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Intersect(L_RECT rect, L_RECT rect1, L_RECT rect2) {
/* 117 */     ltkrnJNI.L_Rect_Intersect(L_RECT.getCPtr(rect), rect, L_RECT.getCPtr(rect1), rect1, L_RECT.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static int L_Rect_IntersectsWith(L_RECT rect, L_RECT testRect) {
/* 121 */     return ltkrnJNI.L_Rect_IntersectsWith(L_RECT.getCPtr(rect), rect, L_RECT.getCPtr(testRect), testRect);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Union(L_RECT rect, L_RECT rect1, L_RECT rect2) {
/* 125 */     ltkrnJNI.L_Rect_Union(L_RECT.getCPtr(rect), rect, L_RECT.getCPtr(rect1), rect1, L_RECT.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_Offset(L_RECT rect, L_SIZE size) {
/* 129 */     ltkrnJNI.L_Rect_Offset(L_RECT.getCPtr(rect), rect, L_SIZE.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static void L_Rect_ToRectD(L_RECT rect, L_RECTD result) {
/* 133 */     ltkrnJNI.L_Rect_ToRectD(L_RECT.getCPtr(rect), rect, L_RECTD.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_PointD_Make(L_POINTD point, double x, double y) {
/* 137 */     ltkrnJNI.L_PointD_Make(L_POINTD.getCPtr(point), point, x, y);
/*     */   }
/*     */ 
/*     */   public static void L_PointD_Empty(L_POINTD point) {
/* 141 */     ltkrnJNI.L_PointD_Empty(L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static int L_PointD_IsEmpty(L_POINTD point) {
/* 145 */     return ltkrnJNI.L_PointD_IsEmpty(L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static int L_PointD_IsEqual(L_POINTD point1, L_POINTD point2) {
/* 149 */     return ltkrnJNI.L_PointD_IsEqual(L_POINTD.getCPtr(point1), point1, L_POINTD.getCPtr(point2), point2);
/*     */   }
/*     */ 
/*     */   public static void L_PointD_Multiply(L_POINTD point, L_MATRIX matrix) {
/* 153 */     ltkrnJNI.L_PointD_Multiply(L_POINTD.getCPtr(point), point, L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_PointD_ToPoint(L_POINTD point, L_POINT result) {
/* 157 */     ltkrnJNI.L_PointD_ToPoint(L_POINTD.getCPtr(point), point, L_POINT.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_SizeD_Make(L_SIZED size, double width, double height) {
/* 161 */     ltkrnJNI.L_SizeD_Make(L_SIZED.getCPtr(size), size, width, height);
/*     */   }
/*     */ 
/*     */   public static void L_SizeD_Empty(L_SIZED size) {
/* 165 */     ltkrnJNI.L_SizeD_Empty(L_SIZED.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_SizeD_IsEmpty(L_SIZED size) {
/* 169 */     return ltkrnJNI.L_SizeD_IsEmpty(L_SIZED.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_SizeD_IsEqual(L_SIZED size1, L_SIZED size2) {
/* 173 */     return ltkrnJNI.L_SizeD_IsEqual(L_SIZED.getCPtr(size1), size1, L_SIZED.getCPtr(size2), size2);
/*     */   }
/*     */ 
/*     */   public static void L_SizeD_ToSize(L_SIZED size, L_SIZE result) {
/* 177 */     ltkrnJNI.L_SizeD_ToSize(L_SIZED.getCPtr(size), size, L_SIZE.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Make(L_RECTD rect, double x, double y, double width, double height) {
/* 181 */     ltkrnJNI.L_RectD_Make(L_RECTD.getCPtr(rect), rect, x, y, width, height);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_FromLTRB(L_RECTD rect, double left, double top, double right, double bottom) {
/* 185 */     ltkrnJNI.L_RectD_FromLTRB(L_RECTD.getCPtr(rect), rect, left, top, right, bottom);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Empty(L_RECTD rect) {
/* 189 */     ltkrnJNI.L_RectD_Empty(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_RectD_IsEmpty(L_RECTD rect) {
/* 193 */     return ltkrnJNI.L_RectD_IsEmpty(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_RectD_IsEqual(L_RECTD rect1, L_RECTD rect2) {
/* 197 */     return ltkrnJNI.L_RectD_IsEqual(L_RECTD.getCPtr(rect1), rect1, L_RECTD.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static double L_RectD_Left(L_RECTD rect) {
/* 201 */     return ltkrnJNI.L_RectD_Left(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static double L_RectD_Right(L_RECTD rect) {
/* 205 */     return ltkrnJNI.L_RectD_Right(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static double L_RectD_Top(L_RECTD rect) {
/* 209 */     return ltkrnJNI.L_RectD_Top(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static double L_RectD_Bottom(L_RECTD rect) {
/* 213 */     return ltkrnJNI.L_RectD_Bottom(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_TopLeft(L_RECTD rect, L_POINTD point) {
/* 217 */     ltkrnJNI.L_RectD_TopLeft(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_TopRight(L_RECTD rect, L_POINTD point) {
/* 221 */     ltkrnJNI.L_RectD_TopRight(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_BottomRight(L_RECTD rect, L_POINTD point) {
/* 225 */     ltkrnJNI.L_RectD_BottomRight(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_BottomLeft(L_RECTD rect, L_POINTD point) {
/* 229 */     ltkrnJNI.L_RectD_BottomLeft(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Location(L_RECTD rect, L_POINTD point) {
/* 233 */     ltkrnJNI.L_RectD_Location(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Size(L_RECTD rect, L_SIZED size) {
/* 237 */     ltkrnJNI.L_RectD_Size(L_RECTD.getCPtr(rect), rect, L_SIZED.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static int L_RectD_ContainsPoint(L_RECTD rect, L_POINTD testPoint) {
/* 241 */     return ltkrnJNI.L_RectD_ContainsPoint(L_RECTD.getCPtr(rect), rect, L_POINTD.getCPtr(testPoint), testPoint);
/*     */   }
/*     */ 
/*     */   public static int L_RectD_ContainsRect(L_RECTD rect, L_RECTD testRect) {
/* 245 */     return ltkrnJNI.L_RectD_ContainsRect(L_RECTD.getCPtr(rect), rect, L_RECTD.getCPtr(testRect), testRect);
/*     */   }
/*     */ 
/*     */   public static int L_RectD_IntersectsWith(L_RECTD rect, L_RECTD testRect) {
/* 249 */     return ltkrnJNI.L_RectD_IntersectsWith(L_RECTD.getCPtr(rect), rect, L_RECTD.getCPtr(testRect), testRect);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Intersect(L_RECTD rect, L_RECTD rect1, L_RECTD rect2) {
/* 253 */     ltkrnJNI.L_RectD_Intersect(L_RECTD.getCPtr(rect), rect, L_RECTD.getCPtr(rect1), rect1, L_RECTD.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Union(L_RECTD rect, L_RECTD rect1, L_RECTD rect2) {
/* 257 */     ltkrnJNI.L_RectD_Union(L_RECTD.getCPtr(rect), rect, L_RECTD.getCPtr(rect1), rect1, L_RECTD.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Offset(L_RECTD rect, L_SIZED size) {
/* 261 */     ltkrnJNI.L_RectD_Offset(L_RECTD.getCPtr(rect), rect, L_SIZED.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Inflate(L_RECTD rect, L_SIZED size) {
/* 265 */     ltkrnJNI.L_RectD_Inflate(L_RECTD.getCPtr(rect), rect, L_SIZED.getCPtr(size), size);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Scale(L_RECTD rect, double scaleX, double scaleY) {
/* 269 */     ltkrnJNI.L_RectD_Scale(L_RECTD.getCPtr(rect), rect, scaleX, scaleY);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_Transform(L_RECTD rect, L_MATRIX matrix) {
/* 273 */     ltkrnJNI.L_RectD_Transform(L_RECTD.getCPtr(rect), rect, L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_RectD_ToRect(L_RECTD rect, L_RECT result) {
/* 277 */     ltkrnJNI.L_RectD_ToRect(L_RECTD.getCPtr(rect), rect, L_RECT.getCPtr(result), result);
/*     */   }
/*     */ 
/*     */   public static void L_LengthD_Make(L_LENGTHD length, double value) {
/* 281 */     ltkrnJNI.L_LengthD_Make(L_LENGTHD.getCPtr(length), length, value);
/*     */   }
/*     */ 
/*     */   public static int L_LengthD_IsEqual(L_LENGTHD length1, L_LENGTHD length2) {
/* 285 */     return ltkrnJNI.L_LengthD_IsEqual(L_LENGTHD.getCPtr(length1), length1, L_LENGTHD.getCPtr(length2), length2);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Set(L_MATRIX matrix, double m11, double m12, double m21, double m22, double offsetX, double offsetY) {
/* 289 */     ltkrnJNI.L_Matrix_Set(L_MATRIX.getCPtr(matrix), matrix, m11, m12, m21, m22, offsetX, offsetY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Identity(L_MATRIX matrix) {
/* 293 */     ltkrnJNI.L_Matrix_Identity(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static int L_Matrix_IsIdentity(L_MATRIX matrix) {
/* 297 */     return ltkrnJNI.L_Matrix_IsIdentity(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_Determinant(L_MATRIX matrix) {
/* 301 */     return ltkrnJNI.L_Matrix_Determinant(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static int L_Matrix_HasInverse(L_MATRIX matrix) {
/* 305 */     return ltkrnJNI.L_Matrix_HasInverse(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetM11(L_MATRIX matrix) {
/* 309 */     return ltkrnJNI.L_Matrix_GetM11(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetM11(L_MATRIX matrix, double value) {
/* 313 */     ltkrnJNI.L_Matrix_SetM11(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetM12(L_MATRIX matrix) {
/* 317 */     return ltkrnJNI.L_Matrix_GetM12(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetM12(L_MATRIX matrix, double value) {
/* 321 */     ltkrnJNI.L_Matrix_SetM12(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetM21(L_MATRIX matrix) {
/* 325 */     return ltkrnJNI.L_Matrix_GetM21(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetM21(L_MATRIX matrix, double value) {
/* 329 */     ltkrnJNI.L_Matrix_SetM21(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetM22(L_MATRIX matrix) {
/* 333 */     return ltkrnJNI.L_Matrix_GetM22(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetM22(L_MATRIX matrix, double value) {
/* 337 */     ltkrnJNI.L_Matrix_SetM22(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetOffsetX(L_MATRIX matrix) {
/* 341 */     return ltkrnJNI.L_Matrix_GetOffsetX(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetOffsetX(L_MATRIX matrix, double value) {
/* 345 */     ltkrnJNI.L_Matrix_SetOffsetX(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static double L_Matrix_GetOffsetY(L_MATRIX matrix) {
/* 349 */     return ltkrnJNI.L_Matrix_GetOffsetY(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SetOffsetY(L_MATRIX matrix, double value) {
/* 353 */     ltkrnJNI.L_Matrix_SetOffsetY(L_MATRIX.getCPtr(matrix), matrix, value);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Multiply(L_MATRIX result, L_MATRIX matrix1, L_MATRIX matrix2) {
/* 357 */     ltkrnJNI.L_Matrix_Multiply(L_MATRIX.getCPtr(result), result, L_MATRIX.getCPtr(matrix1), matrix1, L_MATRIX.getCPtr(matrix2), matrix2);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Append(L_MATRIX result, L_MATRIX matrix) {
/* 361 */     ltkrnJNI.L_Matrix_Append(L_MATRIX.getCPtr(result), result, L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Prepend(L_MATRIX result, L_MATRIX matrix) {
/* 365 */     ltkrnJNI.L_Matrix_Prepend(L_MATRIX.getCPtr(result), result, L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Rotate(L_MATRIX matrix, double degrees) {
/* 369 */     ltkrnJNI.L_Matrix_Rotate(L_MATRIX.getCPtr(matrix), matrix, degrees);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_RotatePrepend(L_MATRIX matrix, double degrees) {
/* 373 */     ltkrnJNI.L_Matrix_RotatePrepend(L_MATRIX.getCPtr(matrix), matrix, degrees);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_RotateAt(L_MATRIX matrix, double degrees, double centerX, double centerY) {
/* 377 */     ltkrnJNI.L_Matrix_RotateAt(L_MATRIX.getCPtr(matrix), matrix, degrees, centerX, centerY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_RotateAtPrepend(L_MATRIX matrix, double degrees, double centerX, double centerY) {
/* 381 */     ltkrnJNI.L_Matrix_RotateAtPrepend(L_MATRIX.getCPtr(matrix), matrix, degrees, centerX, centerY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Scale(L_MATRIX matrix, double scaleX, double scaleY) {
/* 385 */     ltkrnJNI.L_Matrix_Scale(L_MATRIX.getCPtr(matrix), matrix, scaleX, scaleY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_ScalePrepend(L_MATRIX matrix, double scaleX, double scaleY) {
/* 389 */     ltkrnJNI.L_Matrix_ScalePrepend(L_MATRIX.getCPtr(matrix), matrix, scaleX, scaleY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_ScaleAt(L_MATRIX matrix, double scaleX, double scaleY, double centerX, double centerY) {
/* 393 */     ltkrnJNI.L_Matrix_ScaleAt(L_MATRIX.getCPtr(matrix), matrix, scaleX, scaleY, centerX, centerY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_ScaleAtPrepend(L_MATRIX matrix, double scaleX, double scaleY, double centerX, double centerY) {
/* 397 */     ltkrnJNI.L_Matrix_ScaleAtPrepend(L_MATRIX.getCPtr(matrix), matrix, scaleX, scaleY, centerX, centerY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Skew(L_MATRIX matrix, double degreesX, double degreesY) {
/* 401 */     ltkrnJNI.L_Matrix_Skew(L_MATRIX.getCPtr(matrix), matrix, degreesX, degreesY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_SkewPrepend(L_MATRIX matrix, double degreesX, double degreesY) {
/* 405 */     ltkrnJNI.L_Matrix_SkewPrepend(L_MATRIX.getCPtr(matrix), matrix, degreesX, degreesY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_Translate(L_MATRIX matrix, double offsetX, double offsetY) {
/* 409 */     ltkrnJNI.L_Matrix_Translate(L_MATRIX.getCPtr(matrix), matrix, offsetX, offsetY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_TranslatePrepend(L_MATRIX matrix, double offsetX, double offsetY) {
/* 413 */     ltkrnJNI.L_Matrix_TranslatePrepend(L_MATRIX.getCPtr(matrix), matrix, offsetX, offsetY);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_TransformPoint(L_MATRIX matrix, L_POINTD point) {
/* 417 */     ltkrnJNI.L_Matrix_TransformPoint(L_MATRIX.getCPtr(matrix), matrix, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_TransformVector(L_MATRIX matrix, L_POINTD point) {
/* 421 */     ltkrnJNI.L_Matrix_TransformVector(L_MATRIX.getCPtr(matrix), matrix, L_POINTD.getCPtr(point), point);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_TransformPoints(L_MATRIX matrix, L_POINTD points, long count) {
/* 425 */     ltkrnJNI.L_Matrix_TransformPoints(L_MATRIX.getCPtr(matrix), matrix, L_POINTD.getCPtr(points), points, count);
/*     */   }
/*     */ 
/*     */   public static void L_Matrix_TransformRect(L_MATRIX matrix, L_RECTD rect) {
/* 429 */     ltkrnJNI.L_Matrix_TransformRect(L_MATRIX.getCPtr(matrix), matrix, L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_Matrix_Invert(L_MATRIX matrix) {
/* 433 */     return ltkrnJNI.L_Matrix_Invert(L_MATRIX.getCPtr(matrix), matrix);
/*     */   }
/*     */ 
/*     */   public static int L_Matrix_IsEqual(L_MATRIX matrix1, L_MATRIX matrix2) {
/* 437 */     return ltkrnJNI.L_Matrix_IsEqual(L_MATRIX.getCPtr(matrix1), matrix1, L_MATRIX.getCPtr(matrix2), matrix2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_AreClose(double value1, double value2) {
/* 441 */     return ltkrnJNI.L_Double_AreClose(value1, value2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_LessThan(double value1, double value2) {
/* 445 */     return ltkrnJNI.L_Double_LessThan(value1, value2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_GreaterThan(double value1, double value2) {
/* 449 */     return ltkrnJNI.L_Double_GreaterThan(value1, value2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_LessThanOrClose(double value1, double value2) {
/* 453 */     return ltkrnJNI.L_Double_LessThanOrClose(value1, value2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_GreaterThanOrClose(double value1, double value2) {
/* 457 */     return ltkrnJNI.L_Double_GreaterThanOrClose(value1, value2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_IsOne(double value) {
/* 461 */     return ltkrnJNI.L_Double_IsOne(value);
/*     */   }
/*     */ 
/*     */   public static int L_Double_IsZero(double value) {
/* 465 */     return ltkrnJNI.L_Double_IsZero(value);
/*     */   }
/*     */ 
/*     */   public static int L_Double_AreClosePoints(L_POINTD point1, L_POINTD point2) {
/* 469 */     return ltkrnJNI.L_Double_AreClosePoints(L_POINTD.getCPtr(point1), point1, L_POINTD.getCPtr(point2), point2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_AreCloseSizes(L_SIZED size1, L_SIZED size2) {
/* 473 */     return ltkrnJNI.L_Double_AreCloseSizes(L_SIZED.getCPtr(size1), size1, L_SIZED.getCPtr(size2), size2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_AreCloseRects(L_RECTD rect1, L_RECTD rect2) {
/* 477 */     return ltkrnJNI.L_Double_AreCloseRects(L_RECTD.getCPtr(rect1), rect1, L_RECTD.getCPtr(rect2), rect2);
/*     */   }
/*     */ 
/*     */   public static int L_Double_IsBetweenZeroAndOne(double val) {
/* 481 */     return ltkrnJNI.L_Double_IsBetweenZeroAndOne(val);
/*     */   }
/*     */ 
/*     */   public static int L_Double_DoubleToInt(double val) {
/* 485 */     return ltkrnJNI.L_Double_DoubleToInt(val);
/*     */   }
/*     */ 
/*     */   public static int L_Double_RectHasNaN(L_RECTD rect) {
/* 489 */     return ltkrnJNI.L_Double_RectHasNaN(L_RECTD.getCPtr(rect), rect);
/*     */   }
/*     */ 
/*     */   public static int L_Double_IsNaN(double value) {
/* 493 */     return ltkrnJNI.L_Double_IsNaN(value);
/*     */   }
/*     */ 
/*     */   public static int L_Double_IsInfinity(double value) {
/* 497 */     return ltkrnJNI.L_Double_IsInfinity(value);
/*     */   }
/*     */ 
/*     */   public static double L_Double_NormalizeAngle(double angle) {
/* 501 */     return ltkrnJNI.L_Double_NormalizeAngle(angle);
/*     */   }
/*     */ }

/* Location:           /home/wahid/Downloads/docArchive/scan/lib/leadtools.jar
 * Qualified Name:     ltkrn.ltkrn
 * JD-Core Version:    0.6.2
 */