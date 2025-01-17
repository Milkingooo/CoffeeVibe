package com.example.coffeevibe

import android.os.Parcel
import android.os.Parcelable

data class AddItem(
    val name: String,
    val category: String,
    val price: Int,
    val id: String= "",
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeInt(price)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddItem> {
        override fun createFromParcel(parcel: Parcel): AddItem {
            return AddItem(parcel)
        }

        override fun newArray(size: Int): Array<AddItem?> {
            return arrayOfNulls(size)
        }
    }
}