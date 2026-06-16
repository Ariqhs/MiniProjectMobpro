<?php

namespace App\Http\Controllers;

use App\Models\Diary;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class DiaryController extends Controller
{
    public function index(Request $request)
    {
        $email = $request->header('Authorization');
        if (!$email) return response()->json(['message' => 'Unauthorized'], 401);

        $diaries = Diary::where('user_email', $email)->get();

        foreach ($diaries as $diary) {
            $diary->image_url = asset('storage/' . $diary->image_path);
        }

        return response()->json($diaries);
    }

    public function store(Request $request)
    {
        $email = $request->header('Authorization');
        if (!$email) return response()->json(['status' => 'error', 'message' => 'Unauthorized'], 401);

        $request->validate([
            'title' => 'required|string',
            'foreign_amount' => 'required|numeric',
            'currency_code' => 'required|string',
            'converted_idr' => 'required|numeric',
            'image' => 'required|image|max:2048'
        ]);

        $imagePath = $request->file('image')->store('diaries', 'public');

        Diary::create([
            'user_email' => $email,
            'title' => $request->title,
            'foreign_amount' => $request->foreign_amount,
            'currency_code' => $request->currency_code,
            'converted_idr' => $request->converted_idr,
            'image_path' => $imagePath,
        ]);

        return response()->json(['status' => 'success']);
    }
    public function destroy(Request $request, $id)
    {
        $email = $request->header('Authorization');
        $diary = Diary::where('id', $id)->where('user_email', $email)->first();

        if (!$diary) {
            return response()->json(['status' => 'error', 'message' => 'Not found or unauthorized'], 404);
        }

        // Delete image file then delete record
        Storage::disk('public')->delete($diary->image_path);
        $diary->delete();

        return response()->json(['status' => 'success']);
    }
}
